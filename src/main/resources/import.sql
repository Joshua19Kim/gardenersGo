INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('A', 'A', DATE '2004-01-07', 'a@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('B', 'B', DATE '2004-01-07', 'b@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('C', 'C', null, 'c@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('D', null, DATE '2004-01-07', 'd@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('E', null, DATE '2004-01-07', 'd@gmail.com', -1009294837, 'defaultProfilePic.png');

ALTER TABLE relationships ADD CONSTRAINT unique_gardener_friend UNIQUE (gardener_id, friend_id);

INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('Sam', 'Dawson', DATE '2003-01-12', 'test@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('Sam', 'Dawson', DATE '1998-04-07', 'test1@gmail.com', -1009294837, 'defaultProfilePic.png');
INSERT INTO gardener (first_name, last_name, DoB, email, password, profile_picture) VALUES ('Sam', 'Dawson', DATE '2000-11-01', 'test2@gmail.com', -1009294837, 'defaultProfilePic.png');

INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 2, 'accepted');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (4, 1, 'pending');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 3, 'declined');
INSERT INTO relationships (gardener_id, friend_id, status) VALUES (1, 5, 'pending');

INSERT INTO garden (name, location, size) VALUES ('Garden', 'Home', '32');
INSERT INTO garden (name, location, size) VALUES ('Vegetable Garden', 'Kitchen Garden', '35');
INSERT INTO garden (name, location, size) VALUES ('Fruit Garden', 'Backyard', '0');
INSERT INTO garden (name, location, size) VALUES ('Herb Garden', 'Windowsill', '10');

INSERT INTO plant (name, garden_id, count, description, image) VALUES ('My Plant', 1, '2', 'Rose', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('My Plant 2', 1, '29', 'Daisy', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Tomato', 1, '10', 'Red and juicy', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Lavender', 1, '15', 'Fragrant purple flowers', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Basil', 1, '8', 'Aromatic herb used in cooking', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Sunflower', 1, '20', 'Bright and cheerful', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Lily', 1, '12', 'Elegant and fragrant', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Mint', 1, '6', 'Refreshing herb for drinks and salads', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Rosemary', 1, '9', 'Aromatic herb for seasoning', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Daisy', 1, '25', 'White and yellow flower', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Cactus', 1, '5', 'Low-maintenance succulent', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Fern', 1, '18', 'Shade-loving foliage plant', 'placeholder.jpg');

INSERT INTO plant (name, garden_id, count, date_planted, image) VALUES ('My Plant', 2, '0', '12/03/2024', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, image) VALUES ('My Plant 2', 2, '29', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Tomato', 2, '10', 'Red and juicy', 'placeholder.jpg');

INSERT INTO plant (name, garden_id, count, description, date_planted, image) VALUES ('Daisy', 3, '25', 'White and yellow flower', '23/10/2022', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, date_planted, image) VALUES ('Cactus', 3, '0', 'Low-maintenance succulent', '11/11/2001', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Fern', 3, '18', 'Shade-loving foliage plant', 'placeholder.jpg');
INSERT INTO plant (name, garden_id, count, description, image) VALUES ('Rosemary', 3, '9', 'Aromatic herb for seasoning', 'placeholder.jpg');

INSERT INTO garden (gardener_id, name, location, size) VALUES (1, 'Tomato Patch', 'Home', '32');
INSERT INTO garden (gardener_id, name, location, size) VALUES (1, 'Strawberry Garden', 'Kitchen Garden', '35');
INSERT INTO garden (gardener_id, name, location, size) VALUES (2, 'Big Garden', 'Backyard', '0');
INSERT INTO garden (gardener_id, name, location, size) VALUES (3, 'Long Garden', 'Windowsill', '10');

-- email: a@gmail.com
-- password: Password1!
