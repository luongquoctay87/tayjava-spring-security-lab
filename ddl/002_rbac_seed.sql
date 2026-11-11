-- Lesson 20 seed reference (DEV ONLY).
-- The running lab seeds via RbacDataSeeder (BCrypt of demo1234 at startup).
-- Replace :bcrypt_demo1234 below if you apply this script manually.

INSERT INTO permission (id, name) VALUES
  (1, 'course:read'),
  (2, 'course:create'),
  (3, 'course:update:own'),
  (4, 'course:publish'),
  (5, 'course:delete'),
  (6, 'enrollment:create'),
  (7, 'enrollment:read:own'),
  (8, 'user:manage'),
  (9, 'report:view')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role (id, name) VALUES
  (1, 'STUDENT'),
  (2, 'INSTRUCTOR'),
  (3, 'ADMIN')
ON CONFLICT (name) DO NOTHING;

-- STUDENT
INSERT INTO role_permission (role_id, permission_id) VALUES
  (1, 1), (1, 6), (1, 7)
ON CONFLICT DO NOTHING;

-- INSTRUCTOR
INSERT INTO role_permission (role_id, permission_id) VALUES
  (2, 1), (2, 2), (2, 3), (2, 7), (2, 9)
ON CONFLICT DO NOTHING;

-- ADMIN: all
INSERT INTO role_permission (role_id, permission_id) VALUES
  (3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9)
ON CONFLICT DO NOTHING;

-- Password: demo1234 — generate with PasswordEncoder; DO NOT reuse outside lab
INSERT INTO app_user (id, username, password, email, enabled) VALUES
  (1, 'an.student',  ':bcrypt_demo1234', 'an.student@tayjava.com',  TRUE),
  (2, 'bao.teacher', ':bcrypt_demo1234', 'bao.teacher@tayjava.com', TRUE),
  (3, 'chi.teacher', ':bcrypt_demo1234', 'chi.teacher@tayjava.com', TRUE),
  (4, 'root.admin',  ':bcrypt_demo1234', 'root.admin@tayjava.com',  TRUE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_role (user_id, role_id) VALUES
  (1, 1), (2, 2), (3, 2), (4, 3)
ON CONFLICT DO NOTHING;
