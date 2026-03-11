# Student Management System

**Java 17 · Swing · JDBC · MySQL · No frameworks. Manual wiring. Every decision deliberate.**

A desktop application that manages the full academic lifecycle of students within a multi-admin educational institution — authentication, course and grade management, GPA computation, enrollment workflows, transcript archival, and PDF performance reporting.

This is not a tutorial project. It is a production-minded system built from first principles: layered architecture enforced by convention, threading contracts enforced by discipline, and multi-tenant data isolation enforced at the SQL level on every query.

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   GUI Layer (Swing)                  │
│         20 JFrames · SwingWorker throughout          │
│         Input validation · Async coordination        │
└────────────────────────┬────────────────────────────┘
                         │ calls only
┌────────────────────────▼────────────────────────────┐
│                  Service Layer                       │
│      12 classes · Business rules · Transactions      │
│      Validation · Transaction boundaries            │
└────────────────────────┬────────────────────────────┘
                         │ calls only
┌────────────────────────▼────────────────────────────┐
│                    DAO Layer                         │
│        8 classes · SQL execution · JDBC              │
│     Result mapping · Connection lifecycle            │
└────────────────────────┬────────────────────────────┘
                         │ queries only
┌────────────────────────▼────────────────────────────┐
│                 MySQL Database                       │
│   7 tables · UNIQUE constraints · FK relationships   │
│       Multi-tenant schema · adminId on every row     │
└─────────────────────────────────────────────────────┘
```

Dependency flow is strictly one direction. A frame may only call a Service. A Service may only call a DAO. A DAO may only execute SQL. Nothing in a lower layer imports anything from a higher layer. This is not enforced by a framework — it is enforced by discipline, and verified during code review.

---

## Why No Framework

Spring would have been the obvious choice. It was rejected.

For a desktop application with one concurrent session per machine, a DI container introduces more configuration surface area than the problem warrants. Hibernate would have obscured the transaction management that is critical to data integrity here — specifically the multi-step operations where partial failure must roll back atomically. Raw JDBC with PreparedStatement gives full SQL control, no N+1 surprises, and no ORM configuration overhead. The tradeoff is more DAO boilerplate. At this scale, that is acceptable.

The goal was to understand what frameworks abstract before depending on them.

---

## Threading Architecture

The Event Dispatch Thread is the single thread responsible for all Swing rendering and event dispatching. Blocking it freezes the entire UI. This constraint is enforced as a non-negotiable contract across all 20 frames:

```
ALL database and service calls  →  SwingWorker.doInBackground()
ALL UI mutations                →  SwingWorker.done() or SwingUtilities.invokeLater()
Controls disabled on start      →  restored in finally{} block regardless of outcome
```

Every frame that touches the database follows the same pattern. The `finally` block is not optional — it runs on success, failure, and exception. The cursor resets. The controls re-enable. The user is never left with a frozen UI.

---

## Multi-Tenant Data Isolation

Multiple admins share one database. Each admin owns their students, courses, grades, and grading scales. The `adminId` foreign key appears on every table holding admin-owned data.

Isolation is enforced at the SQL level in every DAO query — not left to the caller. A DAO method that reads grades includes `AND adminId = ?` in its WHERE clause. This is not a convention. It was verified during code review, and one method that was missing the admin-scoped JOIN was identified and fixed — a cross-admin data read risk closed before deployment.

---

## Security Design

**Password storage:** SHA-256 with a 128-bit salt generated per user via `SecureRandom`. Stored as `salt:hash` in Base64. Hashing occurs exactly once at registration. A double-hashing bug that permanently broke authentication pre-review was identified and fixed.

**Honest limitation:** SHA-256 is computationally fast (~100M hashes/second on modern hardware), which makes brute-force attacks against weak passwords feasible. PBKDF2 or bcrypt are the correct production choices — both are intentionally slow and configurable to increase resistance as hardware improves. This is documented, not hidden.

**SQL injection:** PreparedStatement throughout all 8 DAOs. Verified during code review.

**Admin PIN generation:** `SecureRandom` with a uniqueness retry loop backed by a `UNIQUE(adminPin)` database constraint. The original implementation used `java.util.Random`. Fixed.

**Uninitialized ID guard:** Every integer ID loaded asynchronously is initialized to `-1` as a sentinel — never `0`, which could be a valid database ID. Every button handler that depends on a loaded ID guards against the sentinel before executing.

---

## State Machine: Student Lifecycle

```
Registration
     │
     ▼
  active ──── deactivated ──── reactivated ──┐
     │                                        │
     ▼                                        │
 graduated ──────────────────────────────────┘
     │
     ▼
 archived  (transcript serialized to JSON, status frozen, destructive)
```

Archival is a multi-step destructive operation: compute GPA → serialize grades to JSON → insert transcript archive → update student status. All four steps run in a single per-admin transaction. Failure for one admin's archive does not roll back another admin's — the correct tradeoff for a multi-admin system.

---

## Database Schema

```
users (id, username, passwordHash, role, forcePasswordChange)
  │
  ├── admins (adminId, userId, firstName, lastName, adminPin)
  │     │
  │     ├── students (studentId, userId, adminId, status, ...)
  │     │     │
  │     │     ├── enrollments (enrollmentId, studentId, courseId, adminId, enrolledBy)
  │     │     └── grades (gradeId, studentId, courseId, adminId, score, gradePoint)
  │     │
  │     ├── courses (courseId, adminId, courseCode, units, ...)
  │     ├── grading_scales (id, adminId, gradingSystem, minScore, maxScore, ...)
  │     └── transcript_archives (archiveId, studentId, adminId, gradesJson, gpa, ...)
```

`UNIQUE(courseCode, adminId)` — course codes are unique per admin, not globally. `UNIQUE(studentId, courseId)` on both enrollments and grades prevents duplicates at the database level.

---

## Code Review Summary

A complete multi-layer review covered all 20 frames, 8 DAOs, 12 service classes, and all utility classes. 15 critical bugs and 8 high-severity issues were identified and remediated.

| Category | Count | Examples |
|---|---|---|
| EDT blocking violations | 9 | Database calls in constructors and button handlers on the Swing thread |
| Uninitialized adminId (defaulting to 0) | 6 | Security risk: operations executing against wrong admin's data |
| Missing `finally` blocks | 4 | Connections left in non-autocommit state after transaction failure |
| Admin data isolation gaps | 2 | Missing `adminId` in WHERE clauses; one cross-admin JOIN risk |
| Resource leaks | 1 | 19 unclosed ResultSets in StudentDAO |
| Logic inversion | 1 | `if(admin != null)` should have been `if(admin == null)` — broken PIN regeneration |
| Parameter order bug | 1 | `enrolledAt`/`enrolledBy` swapped — silent data corruption |
| Constructor missing `initComponents()` | 1 | Every interaction immediately threw NPE |

---

## GPA Calculation

Two modes, both configurable per admin:

```
Weighted:    GPA = Σ(gradePoint × units) / Σ(units)
Unweighted:  GPA = Σ(gradePoint) / courseCount
```

Grade lookup resolves a numeric score against the admin's configured grading scale via range query (`score BETWEEN minScore AND maxScore`). Scores outside defined ranges cannot be graded until the admin defines the appropriate range — a deliberate validation gate, not an oversight.

---

## Known Limitations

These are documented intentionally. Honesty about limitations is part of the design.

**No connection pooling.** Per-call connections work correctly at low concurrency. Under concurrent load, connection exhaustion becomes a risk. HikariCP is the fix — one dependency, minimal configuration change.

**No automated tests.** No unit or integration test suite. All verification during this phase was manual. The service and DAO layers are structured for testability — services accept DAOs as constructor parameters, DAOs accept `Connection` as a method parameter — but the tests do not yet exist.

**No audit logging.** Delete, archive, and grade changes leave no trail. Required for production compliance. The architecture supports it: an `AuditService.log()` call in each destructive service method, backed by an `audit_log` table.

**No session timeout.** Idle sessions remain authenticated indefinitely.

**Grading scale overlap check is application-level only.** The synchronized per-admin lock is correct for a single JVM. It breaks under multi-instance deployment. A MySQL `CHECK` constraint is the durable solution and is documented as a pending improvement.

**No pagination.** Full table load on every refresh. Degrades beyond approximately 10,000 rows. `LIMIT`/`OFFSET` in the DAOs is the fix.

---

## Setup

**Prerequisites:** Java 17, MySQL 8+

```sql
-- Create the database
CREATE DATABASE student_management;
```

```bash
# Clone the repository
git clone https://github.com/Kunzoick/student-management-system.git
cd student-management-system
```

Copy `.env.example` to `.env` and fill in your database credentials. The application reads `DB_URL`, `DB_USER`, and `DB_PASSWORD` from environment variables — no credentials in source.

Run the schema script in `src/main/resources/db/schema.sql` to create all tables and constraints, then compile and run `LoginFrame` as the entry point.

---

## What This Project Demonstrates

Building production-minded software without hiding behind framework abstractions is a deliberate choice. Every threading decision, every transaction boundary, every SQL query is written by hand and reasoned about explicitly. The architecture does not prevent mistakes — it makes mistakes visible and fixable.

The progression from this system to the Trust-Aware Incident API shows what changes when the deployment target changes from a desktop to a distributed API: the layering stays, the transaction thinking stays, the security discipline stays. The framework arrives only after the fundamentals are solid.

---

*Student Management System · v3.1 · December 2025*