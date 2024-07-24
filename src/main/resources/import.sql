INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('A', 'A', DATE '2004-01-07', 'a@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('B', 'B', DATE '2004-01-07', 'b@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('C', 'C', null, 'c@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('D', null, DATE '2004-01-07', 'd@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('E', null, DATE '2004-01-07', 'e@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');

INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test1@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test2@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test3@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test4@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test5@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test6@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test7@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test8@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test9@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('test', 'user', null, 'test10@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');

INSERT INTO authority (gardener_id, role) VALUES (1, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (2, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (3, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (4, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (5, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (6, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (7, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (8, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (9, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (10, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (11, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (12, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (13, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (14, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (15, 'ROLE_USER');

INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN) VALUES (1, 'My First Garden','12 Marquess avenue', null, 'Christchurch','New Zealand', null, '32', false);
INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN) VALUES (2, 'Uni Garden','20 Kirkwood avenue', null, 'Christchurch','New Zealand', null, '100', false);
INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN) VALUES (3, 'Chch Garden','1 Molesworth Street', 'Pipitea', 'Wellington','New Zealand', '6011', '300', false);
INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN) VALUES (1, 'Council Garden','53 Hereford Street', null, 'Christchurch','New Zealand', '8154', '15', false);

INSERT INTO plant (name, garden_id, count, description, image) VALUES ('My Plant', 1, '2', 'Rose', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('My Plant 2', 1, '29', 'Daisy', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Tomato', 1, '10', 'Red and juicy', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Lavender', 1, '15', 'Fragrant purple flowers', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Basil', 1, '8', 'Aromatic herb used in cooking', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Sunflower', 1, '20', 'Bright and cheerful', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Lily', 1, '12', 'Elegant and fragrant', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Mint', 1, '6', 'Refreshing herb for drinks and salads', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Rosemary', 1, '9', 'Aromatic herb for seasoning', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Daisy', 1, '25', 'White and yellow flower', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Cactus', 1, '5', 'Low-maintenance succulent', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Fern', 1, '18', 'Shade-loving foliage plant', '/images/placeholder.jpg');

INSERT INTO plant (name, garden_id, count, date_planted, image) VALUES ('My Plant', 2, '0', '12/03/2024', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, image) VALUES ('My Plant 2', 2, '29', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Tomato', 2, '10', 'Red and juicy', '/images/placeholder.jpg');

INSERT INTO plant (name, garden_id, count, description, date_planted, image) VALUES ('Daisy', 3, '25', 'White and yellow flower', '23/10/2022', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, date_planted, image) VALUES ('Cactus', 3, '0', 'Low-maintenance succulent', '11/11/2001', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Fern', 3, '18', 'Shade-loving foliage plant', '/images/placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Rosemary', 3, '9', 'Aromatic herb for seasoning', '/images/placeholder.jpg');

ALTER TABLE relationships ADD CONSTRAINT unique_gardener_friend UNIQUE (gardener_id, friend_id);

INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 2, 'accepted');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 10, 'accepted');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 11, 'accepted');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (4, 1, 'pending');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (6, 1, 'pending');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 12, 'pending');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 13, 'pending');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 3, 'declined');


INSERT INTO tag (garden , tag_name) VALUES (1, 'herbs');
INSERT INTO tag (garden , tag_name) VALUES (1, 'spices');
INSERT INTO tag (garden , tag_name) VALUES (2, 'fruit');
INSERT INTO tag (garden , tag_name) VALUES (2, 'vegetables');
INSERT INTO tag (garden , tag_name) VALUES (3, 'berries');
INSERT INTO tag (garden , tag_name) VALUES (3, 'outdoor');
INSERT INTO tag (garden , tag_name) VALUES (4, 'indoor');
INSERT INTO tag (garden , tag_name) VALUES (3, 'flowers');

-- email: a@gmail.com
-- password: Password1!
