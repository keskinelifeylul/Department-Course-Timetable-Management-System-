package v3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Automatic Timetable Generation Algorithm, hard and soft constraint
 * checking, manual modification validation, and classroom request email generation.
 */
public class Scheduler {
    private DataLoader dataLoader;
    private Timetable timetable;

    private static final int[] CANDIDATE_HOURS = {9, 13, 10, 14, 11, 15, 12, 16};

    public Scheduler(DataLoader dataLoader, Timetable timetable) {
        this.dataLoader = dataLoader;
        this.timetable = timetable;
    }

    public void generateTimetable() {
        timetable.clear();

        Map<String, Course> courses = dataLoader.getCourses();
        Map<String, Instructor> instructors = dataLoader.getInstructors();
        Map<String, Room> rooms = dataLoader.getRooms();
        List<SpecialRequest> requests = dataLoader.getSpecialRequests();

        for (SpecialRequest req : requests) {
            if ("INSTRUCTOR_UNAVAILABLE".equalsIgnoreCase(req.getConstraintType())) {
                Course c = courses.get(req.getCourseCode());
                if (c != null) {
                    Instructor inst = instructors.get(c.getInstructorId());
                    TimeSlot ts = TimeSlot.parse(req.getValue());
                    if (inst != null && ts != null) {
                        inst.addUnavailableSlot(ts);
                    }
                }
            }
        }

        List<Session> allSessions = new ArrayList<>();
        for (Course c : courses.values()) {
            allSessions.addAll(c.createSessions());
        }
        timetable.setAllSessions(allSessions);

        System.out.println("\n=== AUTOMATIC TIMETABLE GENERATION ===");
        System.out.println("Step 1/2: Processing fixed-time requests...");

        List<Session> fixedSessions = new ArrayList<>();
        for (SpecialRequest req : requests) {
            if ("FIXED_TIME".equalsIgnoreCase(req.getConstraintType())) {
                Session target = timetable.findSessionById(req.getCourseCode() + "-T");
                if (target == null) {
                    target = timetable.findSessionById(req.getCourseCode() + "-L");
                }
                if (target != null && !fixedSessions.contains(target)) {
                    fixedSessions.add(target);
                    processFixedTimeSession(target, req, instructors, rooms);
                }
            }
        }

        System.out.println("\nStep 2/2: Scheduling remaining sessions...");
        for (Session session : allSessions) {
            if (fixedSessions.contains(session)) {
                continue;
            }
            scheduleRemainingSession(session, instructors, rooms, requests);
        }

        int total = allSessions.size();
        int scheduledCount = timetable.getScheduledSessions().size();
        int unscheduledCount = timetable.getUnscheduledSessions().size();
        int warningCount = timetable.getSoftWarnings().size();

        System.out.println("--------------------------------------------------");
        System.out.printf("SUMMARY  Total sessions: %d | Scheduled: %d | Unscheduled: %d | Warnings: %d%n",
                total, scheduledCount, unscheduledCount, warningCount);
        System.out.println("--------------------------------------------------");
    }

    private void processFixedTimeSession(Session session, SpecialRequest req,
                                         Map<String, Instructor> instructors,
                                         Map<String, Room> rooms) {
        TimeSlot fixedSlot = TimeSlot.parse(req.getValue());
        if (fixedSlot == null || !fixedSlot.isWithinWorkingHours()) {
            session.unassign("CONSTRAINT", "HC-04/HC-08: invalid fixed time slot " + req.getValue());
            System.out.printf("  [FAIL] %-10s Fixed time %s violates HC-04 (working hours)%n",
                    session.getSessionId(), req.getValue());
            return;
        }

        if (fixedSlot.getDuration() != session.getDurationHours()) {
            fixedSlot = new TimeSlot(fixedSlot.getDay(), fixedSlot.getStartHour(),
                    fixedSlot.getStartHour() + session.getDurationHours());
        }

        String timeConflict = checkSlotHardConstraints(session, fixedSlot, instructors);
        if (timeConflict != null) {
            session.unassign("CONSTRAINT", String.format("Fixed time (%s) conflicts with %s",
                    fixedSlot.getDay(), timeConflict));
            System.out.printf("  [FAIL] %-10s Fixed time %s violates %s%n",
                    session.getSessionId(), fixedSlot, timeConflict);
            return;
        }

        List<Room> suitableRooms = getCapableRooms(session, rooms);
        if (suitableRooms.isEmpty()) {
            String reason = String.format("HC-05: no %s with capacity >= %d",
                    session.getRequiredRoomType().toLowerCase(), session.getStudentCount());
            session.unassign("NO ROOM", reason);
            System.out.printf("  [FAIL] %-10s %s%n", session.getSessionId(), reason);
            return;
        }

        for (Room room : suitableRooms) {
            String roomConflict = checkRoomHardConstraints(session, fixedSlot, room);
            if (roomConflict == null) {
                session.assign(fixedSlot, room);
                session.setPlacementNote("(fixed by request)");
                System.out.printf("  [OK]   %-10s %-14s %-5s (fixed by request)%n",
                        session.getSessionId(), fixedSlot, room.getRoomId());
                checkAndRecordSoftConstraints(session, fixedSlot, instructors, true);
                return;
            }
        }

        session.unassign("NO ROOM", "No available room at fixed time " + fixedSlot);
        System.out.printf("  [FAIL] %-10s No available room at fixed time %s%n",
                session.getSessionId(), fixedSlot);
    }

    private void scheduleRemainingSession(Session session,
                                          Map<String, Instructor> instructors,
                                          Map<String, Room> rooms,
                                          List<SpecialRequest> requests) {
        List<Room> capableRooms = getCapableRooms(session, rooms);
        if (capableRooms.isEmpty()) {
            String reason = String.format("No %s with capacity >= %d",
                    session.getRequiredRoomType().toLowerCase(), session.getStudentCount());
            session.unassign("NO ROOM", reason);
            System.out.printf("  [FAIL] %-10s HC-05: no %s with capacity >= %d%n",
                    session.getSessionId(), session.getRequiredRoomType().toLowerCase(), session.getStudentCount());
            return;
        }

        SpecialRequest roomReq = findRoomRequest(session, requests);
        if (roomReq != null) {
            Room reqRoom = rooms.get(roomReq.getValue());
            if (reqRoom != null && reqRoom.matchesType(session.getRequiredRoomType())) {
                if (reqRoom.hasSufficientCapacity(session.getStudentCount())) {
                    capableRooms.remove(reqRoom);
                    capableRooms.add(0, reqRoom);
                    if (roomReq.isMandatory()) {
                        capableRooms.clear();
                        capableRooms.add(reqRoom);
                    }
                } else if (roomReq.isMandatory()) {
                    String reason = String.format("HC-05: requested room %s capacity (%d) < %d",
                            reqRoom.getRoomId(), reqRoom.getCapacity(), session.getStudentCount());
                    session.unassign("CONSTRAINT", reason);
                    System.out.printf("  [FAIL] %-10s %s%n", session.getSessionId(), reason);
                    return;
                }
            }
        }

        int rejectedAttempts = 0;

        for (String day : TimeSlot.WORKING_DAYS) {
            for (int startHour : CANDIDATE_HOURS) {
                int endHour = startHour + session.getDurationHours();
                if (endHour > TimeSlot.DAY_END_HOUR) {
                    continue;
                }
                TimeSlot candidateSlot = new TimeSlot(day, startHour, endHour);

                for (Room room : capableRooms) {
                    String hardViolation = checkAllHardConstraints(session, candidateSlot, room, instructors);
                    if (hardViolation != null) {
                        rejectedAttempts++;
                        continue;
                    }
                    if (satisfiesAllSoftConstraints(session, candidateSlot, instructors, requests)) {
                        session.assign(candidateSlot, room);
                        session.setRejectedAttempts(rejectedAttempts);
                        String note = buildPlacementNote(rejectedAttempts, roomReq, room);
                        session.setPlacementNote(note);
                        System.out.printf("  [OK]   %-10s %-14s %-5s%s%n",
                                session.getSessionId(), candidateSlot, room.getRoomId(),
                                note.isEmpty() ? "" : " " + note);
                        return;
                    }
                }
            }
        }

        rejectedAttempts = 0;
        for (String day : TimeSlot.WORKING_DAYS) {
            for (int startHour : CANDIDATE_HOURS) {
                int endHour = startHour + session.getDurationHours();
                if (endHour > TimeSlot.DAY_END_HOUR) {
                    continue;
                }
                TimeSlot candidateSlot = new TimeSlot(day, startHour, endHour);

                for (Room room : capableRooms) {
                    String hardViolation = checkAllHardConstraints(session, candidateSlot, room, instructors);
                    if (hardViolation != null) {
                        rejectedAttempts++;
                        continue;
                    }
                    session.assign(candidateSlot, room);
                    session.setRejectedAttempts(rejectedAttempts);
                    String note = buildPlacementNote(rejectedAttempts, roomReq, room);
                    session.setPlacementNote(note);
                    System.out.printf("  [OK]   %-10s %-14s %-5s%s%n",
                            session.getSessionId(), candidateSlot, room.getRoomId(),
                            note.isEmpty() ? "" : " " + note);
                    checkAndRecordSoftConstraints(session, candidateSlot, instructors, true);
                    return;
                }
            }
        }

        session.unassign("CONSTRAINT", "All candidate time slots and rooms conflicted with hard constraints");
        System.out.printf("  [FAIL] %-10s Could not find a valid slot/room after %d attempts%n",
                session.getSessionId(), rejectedAttempts);
    }

    private String buildPlacementNote(int rejectedAttempts, SpecialRequest roomReq, Room assignedRoom) {
        if (roomReq != null && assignedRoom.getRoomId().equalsIgnoreCase(roomReq.getValue())) {
            return "(special request: must use " + assignedRoom.getRoomId() + ")";
        }
        if (rejectedAttempts > 0) {
            return String.format("(after %d rejected attempt%s)", rejectedAttempts, rejectedAttempts == 1 ? "" : "s");
        }
        return "";
    }

    private SpecialRequest findRoomRequest(Session session, List<SpecialRequest> requests) {
        for (SpecialRequest req : requests) {
            if ("REQUIRED_ROOM".equalsIgnoreCase(req.getConstraintType())
                    && req.getCourseCode().equalsIgnoreCase(session.getCourse().getCourseCode())) {
                Room r = dataLoader.getRooms().get(req.getValue());
                if (r != null && r.matchesType(session.getRequiredRoomType())) {
                    return req;
                }
            }
        }
        return null;
    }

    private boolean allowsLabBeforeTheory(Session session, List<SpecialRequest> requests) {
        for (SpecialRequest req : requests) {
            if ("LAB_BEFORE_THEORY".equalsIgnoreCase(req.getConstraintType())
                    && req.getCourseCode().equalsIgnoreCase(session.getCourse().getCourseCode())) {
                return true;
            }
        }
        return false;
    }

    private List<Room> getCapableRooms(Session session, Map<String, Room> rooms) {
        List<Room> capable = new ArrayList<>();
        for (Room r : rooms.values()) {
            if (r.matchesType(session.getRequiredRoomType()) && r.hasSufficientCapacity(session.getStudentCount())) {
                capable.add(r);
            }
        }
        capable.sort((r1, r2) -> Integer.compare(r1.getCapacity(), r2.getCapacity()));
        return capable;
    }

    public String checkAllHardConstraints(Session session, TimeSlot slot, Room room,
                                          Map<String, Instructor> instructors) {
        if (slot == null || !slot.isWithinWorkingHours()) {
            return "HC-04 Working hours: session must be between 09:00 and 17:00, Mon-Fri";
        }
        if (room == null) {
            return "HC-06 Room does not exist";
        }
        if (!room.matchesType(session.getRequiredRoomType())) {
            return String.format("HC-06 Room type mismatch: %s is a %s, but %s requires a %s",
                    room.getRoomId(), room.getRoomType(), session.getSessionId(), session.getRequiredRoomType());
        }
        if (!room.hasSufficientCapacity(session.getStudentCount())) {
            return String.format("HC-05 Room capacity: %s has capacity %d, but %s has %d students",
                    room.getRoomId(), room.getCapacity(), session.getSessionId(), session.getStudentCount());
        }

        String roomConflict = checkRoomHardConstraints(session, slot, room);
        if (roomConflict != null) {
            return roomConflict;
        }

        return checkSlotHardConstraints(session, slot, instructors);
    }

    private String checkRoomHardConstraints(Session session, TimeSlot slot, Room room) {
        if (!room.isAvailable(slot)) {
            return String.format("HC-09 Room availability: %s is unavailable during %s", room.getRoomId(), slot);
        }
        for (Session other : timetable.getScheduledSessions()) {
            if (other == session) {
                continue;
            }
            if (other.getAssignedRoom().getRoomId().equalsIgnoreCase(room.getRoomId())
                    && other.getAssignedTimeSlot().overlapsWith(slot)) {
                return String.format("HC-02 Room conflict: %s is used by %s on %s",
                        room.getRoomId(), other.getSessionId(), other.getAssignedTimeSlot());
            }
        }
        return null;
    }

    private String checkSlotHardConstraints(Session session, TimeSlot slot, Map<String, Instructor> instructors) {
        Instructor inst = instructors.get(session.getInstructorId());
        if (inst != null && !inst.isAvailable(slot)) {
            return String.format("HC-07: instructor %s is unavailable on %s",
                    inst.getName(), TimeSlot.getFullDayName(slot.getDay()));
        }

        for (Session other : timetable.getScheduledSessions()) {
            if (other == session) {
                continue;
            }
            if (other.getAssignedTimeSlot().overlapsWith(slot)) {
                if (other.getInstructorId().equalsIgnoreCase(session.getInstructorId())) {
                    String instName = (inst != null) ? inst.getName() : session.getInstructorId();
                    return String.format("HC-01 Instructor conflict: %s is teaching %s on %s",
                            instName, other.getSessionId(), other.getAssignedTimeSlot());
                }
                if (other.getYearGroup() == session.getYearGroup()) {
                    return String.format("HC-03 Year-group conflict: Year %d already has %s on %s",
                            session.getYearGroup(), other.getSessionId(), other.getAssignedTimeSlot());
                }
            }
        }
        return null;
    }

    private boolean satisfiesAllSoftConstraints(Session session, TimeSlot slot,
                                                Map<String, Instructor> instructors,
                                                List<SpecialRequest> requests) {
        if (session.isLab() && !allowsLabBeforeTheory(session, requests)) {
            Session theorySession = timetable.findSessionById(session.getCourse().getCourseCode() + "-T");
            if (theorySession != null && theorySession.isScheduled()) {
                if (!slot.isAfter(theorySession.getAssignedTimeSlot())) {
                    return false;
                }
            }
        }

        Instructor inst = instructors.get(session.getInstructorId());
        if (inst != null && !inst.satisfiesPreference(slot)) {
            return false;
        }

        if (calculateDayGapHours(session, slot) >= 2) {
            return false;
        }

        return true;
    }

    public List<String> evaluateSoftConstraints(Session session, TimeSlot slot,
                                                Map<String, Instructor> instructors) {
        List<String> warnings = new ArrayList<>();

        if (session.isLab() && !allowsLabBeforeTheory(session, dataLoader.getSpecialRequests())) {
            Session theory = timetable.findSessionById(session.getCourse().getCourseCode() + "-T");
            if (theory != null && theory.isScheduled() && !slot.isAfter(theory.getAssignedTimeSlot())) {
                warnings.add(String.format("SC-01 %s placed on %s before theoretical session (%s).",
                        session.getSessionId(), slot, theory.getAssignedTimeSlot()));
            }
        }

        Instructor inst = instructors.get(session.getInstructorId());
        if (inst != null && !inst.satisfiesPreference(slot)) {
            String prefDesc = "MORNING".equalsIgnoreCase(inst.getPreferredTime()) ? "mornings" : "afternoons";
            warnings.add(String.format("SC-02: %s prefers %s; %s placed on %s %02d:00",
                    inst.getName(), prefDesc, session.getSessionId(), slot.getDay(), slot.getStartHour()));
        }

        int gap = calculateDayGapHours(session, slot);
        if (gap >= 2) {
            int[] gapBounds = findDayGapBounds(session, slot);
            warnings.add(String.format("SC-03 Year %d will have a %d-hour gap on %s (%02d:00-%02d:00).",
                    session.getYearGroup(), gap, TimeSlot.getFullDayName(slot.getDay()), gapBounds[0], gapBounds[1]));
        }

        return warnings;
    }

    private void checkAndRecordSoftConstraints(Session session, TimeSlot slot,
                                               Map<String, Instructor> instructors,
                                               boolean printToConsole) {
        List<String> warnings = evaluateSoftConstraints(session, slot, instructors);
        for (String w : warnings) {
            timetable.addSoftWarning(w);
            if (printToConsole) {
                System.out.printf("  [WARN] %-10s %s%n", session.getSessionId(), w);
            }
        }
    }

    private int calculateDayGapHours(Session session, TimeSlot candidateSlot) {
        int[] bounds = findDayGapBounds(session, candidateSlot);
        if (bounds == null) {
            return 0;
        }
        return bounds[1] - bounds[0];
    }

    private int[] findDayGapBounds(Session session, TimeSlot candidateSlot) {
        int maxGap = 0;
        int[] bestBounds = null;
        for (Session other : timetable.getScheduledSessions()) {
            if (other == session) {
                continue;
            }
            if (other.getYearGroup() == session.getYearGroup()
                    && other.getAssignedTimeSlot().getDay().equalsIgnoreCase(candidateSlot.getDay())) {
                TimeSlot otherSlot = other.getAssignedTimeSlot();
                int gapStart = Math.min(otherSlot.getEndHour(), candidateSlot.getEndHour());
                int gapEnd = Math.max(otherSlot.getStartHour(), candidateSlot.getStartHour());
                int gap = gapEnd - gapStart;
                if (gap > maxGap) {
                    maxGap = gap;
                    bestBounds = new int[]{gapStart, gapEnd};
                }
            }
        }
        return bestBounds;
    }

    public TimeSlot findSuggestedTimeForRoomRequest(Session session) {
        Map<String, Instructor> instructors = dataLoader.getInstructors();
        for (String day : TimeSlot.WORKING_DAYS) {
            for (int startHour : CANDIDATE_HOURS) {
                int endHour = startHour + session.getDurationHours();
                if (endHour > TimeSlot.DAY_END_HOUR) {
                    continue;
                }
                TimeSlot candidate = new TimeSlot(day, startHour, endHour);
                if (checkSlotHardConstraints(session, candidate, instructors) == null) {
                    return candidate;
                }
            }
        }
        return new TimeSlot("Tue", 13, 13 + session.getDurationHours());
    }

    public String generateClassroomRequestEmail(Session session, TimeSlot suggestedSlot) {
        String sessionType = session.isLab() ? "Laboratory" : "Theoretical";
        String reqRoomDesc = session.isLab() ? "Computer Laboratory" : "Lecture Classroom";
        String dayFull = TimeSlot.getFullDayName(suggestedSlot.getDay());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Subject: Classroom Request - %s%n%n", session.getCourse().getCourseCode()));
        sb.append("Dear Student Affairs Office,\n\n");
        sb.append("We are preparing the department course timetable for the upcoming semester.\n");
        sb.append("A suitable classroom could not be assigned for the following session:\n\n");
        sb.append(String.format("Course: %s - %s (%s)%n",
                session.getCourse().getCourseCode(), session.getCourse().getTitle(), sessionType));
        sb.append(String.format("Number of Students: %d%n", session.getStudentCount()));
        sb.append(String.format("Required Room Type: %s%n", reqRoomDesc));
        sb.append(String.format("Preferred Time: %s, %s%n%n", dayFull, suggestedSlot.formatHours()));
        sb.append("We kindly request your assistance in identifying an available room\n");
        sb.append("that meets these requirements.\n\n");
        sb.append("Best regards,\n");
        sb.append("Department Timetable Coordinator\n");
        return sb.toString();
    }

    public String saveClassroomRequestEmail(Session session, String emailContent, String outputDir) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "classroom_request_" + session.getCourse().getCourseCode() + ".txt";
        File file = new File(dir, fileName);
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.print(emailContent);
        }
        return outputDir + "/" + fileName;
    }
}

