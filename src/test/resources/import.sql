-- DO NOT DELETE THIS FILE
-- This file is used for @DataJpaTests and can be left empty
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('A', 'A', DATE '2004-01-07', 'a@gmail.com', '$2a$10$5JkvOc65rJFRmpjOIeEOi.lBGL6ttCiV6dYHdMR2Cdlxu8s1cs26O', '/images/defaultProfilePic.png');
INSERT INTO authority (gardener_id, role) VALUES (1, 'ROLE_USER');
