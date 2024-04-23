ALTER TABLE relationships ADD CONSTRAINT unique_gardener_friend UNIQUE (gardener_id, friend_id);

INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('A', 'A', DATE '2004-01-07', 'a@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('B', 'B', DATE '2004-01-07', 'b@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('C', 'C', null, 'c@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('D', null, DATE '2004-01-07', 'd@gmail.com', -1009294837, 'defaultProfilePic.png');

INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('Sam', 'Dawson', DATE '2003-01-12', 'test@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('Sam', 'Dawson', DATE '1998-04-07', 'test1@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('Sam', 'Dawson', DATE '2000-11-01', 'test2@gmail.com', -1009294837, 'defaultProfilePic.png');

INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 2, 'accepted');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (4, 1, 'pending');
