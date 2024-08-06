-- DO NOT DELETE THIS FILE
-- This file is used for @DataJpaTests and can be left empty
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture, bad_word_count) VALUES ('A', 'A', DATE '2004-01-07', 'a@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png', 0);
INSERT INTO authority (gardener_id, role) VALUES (1, 'ROLE_USER');

INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture, bad_word_count) VALUES ('B', 'B', DATE '2004-01-07', 'b@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png', 0);
INSERT INTO authority (gardener_id, role) VALUES (2, 'ROLE_USER');

INSERT INTO garden (gardener_id, name, location, suburb, city, country, postcode, size, PUBLIC_GARDEN, creation_date) VALUES (1, 'My First Garden','12 Marquess avenue', null, 'Christchurch','New Zealand', null, '32', true, '2024-12-03');
