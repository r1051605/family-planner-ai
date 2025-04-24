-- This script runs on application startup when spring.jpa.hibernate.ddl-auto=create or create-drop

-- Create default families
INSERT INTO families (id, name, creation_date) VALUES
                                                   (1, 'Smith Family', '2025-04-20 10:00:00'),
                                                   (2, 'Johnson Family', '2025-04-21 12:00:00');

-- Create users (password is 'password' encoded with BCrypt)
-- Add admin parent user
INSERT INTO users (id, username, password, email, role, family_id, theme_preference) VALUES
    (8, 'admin', '$2a$10$Dow1H3RzSBR5p/SO7u7a1uLLEJv/YeU7fMIVel9NHYi2rf/r7ORgu', 'admin@example.com', 'PARENT', 1, NULL);

-- PARENT role users
INSERT INTO users (id, username, password, email, role, family_id, theme_preference) VALUES
                                                                                         (1, 'john_smith', '$2a$10$rXBaFfe7YYpWN/GMtKlrqeptjhSy0YUCe7OWR4pdXNQfJL2nDgXIq', 'john@example.com', 'PARENT', 1, NULL),
                                                                                         (2, 'mary_smith', '$2a$10$rXBaFfe7YYpWN/GMtKlrqeptjhSy0YUCe7OWR4pdXNQfJL2nDgXIq', 'mary@example.com', 'PARENT', 1, NULL),
                                                                                         (3, 'david_johnson', '$2a$10$rXBaFfe7YYpWN/GMtKlrqeptjhSy0YUCe7OWR4pdXNQfJL2nDgXIq', 'david@example.com', 'PARENT', 2, NULL);

-- TEEN role users
INSERT INTO users (id, username, password, email, role, family_id, theme_preference) VALUES
                                                                                         (4, 'emma_smith', '$2a$10$rXBaFfe7YYpWN/GMtKlrqeptjhSy0YUCe7OWR4pdXNQfJL2nDgXIq', 'emma@example.com', 'TEEN', 1, NULL),
                                                                                         (5, 'michael_johnson', '$2a$10$rXBaFfe7YYpWN/GMtKlrqeptjhSy0YUCe7OWR4pdXNQfJL2nDgXIq', 'michael@example.com', 'TEEN', 2, NULL);

-- KID role users
INSERT INTO users (id, username, password, email, role, family_id, theme_preference) VALUES
                                                                                         (6, 'sarah_smith', '$2a$10$rXBaFfe7YYpWN/GMtKlrqeptjhSy0YUCe7OWR4pdXNQfJL2nDgXIq', 'sarah@example.com', 'KID', 1, NULL),
                                                                                         (7, 'james_johnson', '$2a$10$rXBaFfe7YYpWN/GMtKlrqeptjhSy0YUCe7OWR4pdXNQfJL2nDgXIq', 'james@example.com', 'KID', 2, NULL);

-- Set family owners
UPDATE families SET owner_id = 1 WHERE id = 1;
UPDATE families SET owner_id = 3 WHERE id = 2;

-- Create user statuses
INSERT INTO user_status (id, user_id, status, last_updated) VALUES
                                                                (1, 1, 'HOME', '2025-04-22 08:00:00'),
                                                                (2, 2, 'WORKING', '2025-04-22 09:00:00'),
                                                                (3, 3, 'HOME', '2025-04-22 08:30:00'),
                                                                (4, 4, 'SCHOOL', '2025-04-22 07:45:00'),
                                                                (5, 5, 'SCHOOL', '2025-04-22 07:30:00'),
                                                                (6, 6, 'HOME', '2025-04-22 16:00:00'),
                                                                (7, 7, 'OUT_WITH_FRIENDS', '2025-04-22 15:30:00');

-- Create tasks for Smith family
INSERT INTO tasks (id, title, description, due_date, assigned_to, created_by, status, family_id) VALUES
                                                                                                     (1, 'Clean the kitchen', 'Wipe counters, clean sink, sweep floor', '2025-04-23 18:00:00', 4, 1, 'PENDING', 1),
                                                                                                     (2, 'Take out trash', 'Take trash to the curb', '2025-04-22 20:00:00', 6, 1, 'COMPLETED', 1),
                                                                                                     (3, 'Grocery shopping', 'Get items from shopping list', '2025-04-24 12:00:00', 2, 2, 'PENDING', 1),
                                                                                                     (4, 'Mow the lawn', 'Cut grass in front and back yard', '2025-04-25 15:00:00', NULL, 1, 'PENDING', 1);

-- Create tasks for Johnson family
INSERT INTO tasks (id, title, description, due_date, assigned_to, created_by, status, family_id) VALUES
                                                                                                     (5, 'Vacuum living room', 'Vacuum carpet and furniture', '2025-04-23 17:00:00', 5, 3, 'IN_PROGRESS', 2),
                                                                                                     (6, 'Do laundry', 'Wash clothes and towels', '2025-04-22 19:00:00', 3, 3, 'COMPLETED', 2),
                                                                                                     (7, 'Clean bathroom', 'Clean toilet, sink, and shower', '2025-04-24 16:00:00', NULL, 3, 'PENDING', 2);

-- Create shopping items for Smith family
INSERT INTO shopping_items (id, name, quantity, bought, category, added_by, family_id) VALUES
                                                                                           (1, 'Milk', 2, false, 'Dairy & Eggs', 1, 1),
                                                                                           (2, 'Bread', 1, false, 'Bakery', 2, 1),
                                                                                           (3, 'Apples', 6, false, 'Fruits & Vegetables', 4, 1),
                                                                                           (4, 'Toilet Paper', 1, true, 'Household', 2, 1);

-- Create shopping items for Johnson family
INSERT INTO shopping_items (id, name, quantity, bought, category, added_by, family_id) VALUES
                                                                                           (5, 'Chicken Breasts', 1, false, 'Meat & Fish', 3, 2),
                                                                                           (6, 'Pasta', 2, false, 'Pantry', 5, 2),
                                                                                           (7, 'Bananas', 5, true, 'Fruits & Vegetables', 3, 2);

-- Create events for Smith family
INSERT INTO events (id, title, description, start_time, end_time, created_by, family_id, is_holiday) VALUES
                                                                                                         (1, 'Emma''s Soccer Game', 'At the community field', '2025-04-25 16:00:00', '2025-04-25 17:30:00', 1, 1, false),
                                                                                                         (2, 'Dentist Appointment', 'For Sarah, Dr. Johnson', '2025-04-23 14:00:00', '2025-04-23 15:00:00', 2, 1, false);

-- Create events for Johnson family
INSERT INTO events (id, title, description, start_time, end_time, created_by, family_id, is_holiday) VALUES
                                                                                                         (3, 'Family Dinner', 'At grandma''s house', '2025-04-26 18:00:00', '2025-04-26 20:00:00', 3, 2, false),
                                                                                                         (4, 'Michael''s Basketball Practice', 'School gym', '2025-04-24 17:00:00', '2025-04-24 18:30:00', 3, 2, false);

-- Create groups for Smith family
INSERT INTO groups (id, name, description, created_by, family_id) VALUES
                                                                      (1, 'Close Friends', 'Our regular visitors', 1, 1),
                                                                      (2, 'Extended Family', 'Relatives who visit occasionally', 2, 1);

-- Create groups for Johnson family
INSERT INTO groups (id, name, description, created_by, family_id) VALUES
                                                                      (3, 'Kids'' Friends', 'Friends of the children', 3, 2),
                                                                      (4, 'Neighbors', 'People from the neighborhood', 3, 2);

-- Create group members for Smith family
INSERT INTO group_members (id, group_id, name, visit_date, added_by) VALUES
                                                                         (1, 1, 'Mike & Lisa', '2025-04-27 18:00:00', 1),
                                                                         (2, 1, 'Bob & Carol', '2025-05-01 19:00:00', 2),
                                                                         (3, 2, 'Uncle Jim', '2025-04-30 12:00:00', 2);

-- Create group members for Johnson family
INSERT INTO group_members (id, group_id, name, visit_date, added_by) VALUES
                                                                         (4, 3, 'Tom (Michael''s friend)', '2025-04-28 15:00:00', 3),
                                                                         (5, 3, 'Lucy (James''s friend)', '2025-04-29 16:00:00', 3),
                                                                         (6, 4, 'The Wilsons', '2025-05-02 18:00:00', 3);

-- Create chat messages for Smith family
INSERT INTO chat_messages (id, sender_id, content, timestamp, family_id) VALUES
                                                                             (1, 1, 'Don''t forget Emma''s soccer game on Friday!', '2025-04-22 09:15:00', 1),
                                                                             (2, 2, 'I''ve added milk and bread to the shopping list.', '2025-04-22 10:30:00', 1),
                                                                             (3, 4, 'I''ll clean the kitchen after dinner.', '2025-04-22 12:45:00', 1);

-- Create chat messages for Johnson family
INSERT INTO chat_messages (id, sender_id, content, timestamp, family_id) VALUES
                                                                             (4, 3, 'Remember we have dinner at grandma''s on Saturday.', '2025-04-22 09:00:00', 2),
                                                                             (5, 5, 'I''ve finished vacuuming the living room.', '2025-04-22 11:00:00', 2),
                                                                             (6, 3, 'Don''t forget to take your homework, James!', '2025-04-22 07:15:00', 2);