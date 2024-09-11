-- DO NOT DELETE THIS FILE
-- This file is used for @DataJpaTests and can be left empty
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture, bad_word_count) VALUES ('A', 'A', DATE '2004-01-07', 'a@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png', 0);
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture, bad_word_count) VALUES ('B', 'B', DATE '2004-01-07', 'b@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png', 0);
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture, bad_word_count) VALUES ('C', 'C', null, 'c@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png', 0);
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture, bad_word_count) VALUES ('D', null, DATE '2004-01-07', 'd@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png', 0);
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture, bad_word_count) VALUES ('E', null, DATE '2004-01-07', 'e@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png', 0);

INSERT INTO authority (gardener_id, role) VALUES (1, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (2, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (3, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (4, 'ROLE_USER');
INSERT INTO authority (gardener_id, role) VALUES (5, 'ROLE_USER');

INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN, creation_date) VALUES (1, 'My First Garden','12 Marquess avenue', null, 'Christchurch','New Zealand', null, '32', true, '2024-12-03');
INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN, creation_date) VALUES (2, 'Uni Garden','20 Kirkwood avenue', null, 'Christchurch','New Zealand', null, '100', false, '2024-12-04');
INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN, creation_date) VALUES (3, 'Chch Garden','1 Molesworth Street', 'Pipitea', 'Wellington','New Zealand', '6011', '300', false, '2024-12-05');
INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN, creation_date) VALUES (1, 'Council Garden','53 Hereford Street', null, 'Christchurch','New Zealand', '8154', '15', true, '2024-12-02');
INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN, creation_date) VALUES (2, 'Rosewood Park', '12 Elm Street', 'Rosewood', 'Los Angeles', 'USA', '90001', '10', true, '2024-06-15');

INSERT INTO main_page_layout (format, gardener_id, widget_small_one, widget_small_two, widget_tall, widget_wide, widgets_enabled) VALUES ('1 2 3', 1, 'recentGardens', 'recentPlants', 'myGardens', 'myFriends', '1 1 1 1');
INSERT INTO main_page_layout (format, gardener_id, widget_small_one, widget_small_two, widget_tall, widget_wide, widgets_enabled) VALUES ('1 2 3', 2, 'recentGardens', 'recentPlants', 'myGardens', 'myFriends', '1 1 1 1');
