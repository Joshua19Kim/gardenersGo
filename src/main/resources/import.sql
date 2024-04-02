INSERT INTO garden (name, location, size) VALUES ('Garden', 'Home', '32');
INSERT INTO garden (name, location, size) VALUES ('Vegetable Garden', 'Kitchen Garden', '35');
INSERT INTO garden (name, location, size) VALUES ('Fruit Garden', 'Backyard', '0');
INSERT INTO garden (name, location, size) VALUES ('Herb Garden', 'Windowsill', '10');

INSERT INTO plant (name, garden_id, count, description) VALUES ('My Plant', 1, '2', 'Rose');
INSERT INTO plant (name, garden_id, count, description) VALUES ('My Plant 2', 1, '29', 'Daisy');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Tomato', 1, '10', 'Red and juicy');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Lavender', 1, '15', 'Fragrant purple flowers');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Basil', 1, '8', 'Aromatic herb used in cooking');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Sunflower', 1, '20', 'Bright and cheerful');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Lily', 1, '12', 'Elegant and fragrant');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Mint', 1, '6', 'Refreshing herb for drinks and salads');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Rosemary', 1, '9', 'Aromatic herb for seasoning');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Daisy', 1, '25', 'White and yellow flower');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Cactus', 1, '5', 'Low-maintenance succulent');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Fern', 1, '18', 'Shade-loving foliage plant');

INSERT INTO plant (name, garden_id, count, date_planted) VALUES ('My Plant', 2, '0', '12/03/2024');
INSERT INTO plant (name, garden_id, count) VALUES ('My Plant 2', 2, '29');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Tomato', 2, '10', 'Red and juicy');

INSERT INTO plant (name, garden_id, count, description, date_planted) VALUES ('Daisy', 3, '25', 'White and yellow flower', '23/10/2022');
INSERT INTO plant (name, garden_id, count, description, date_planted) VALUES ('Cactus', 3, '0', 'Low-maintenance succulent', '11/11/2001');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Fern', 3, '18', 'Shade-loving foliage plant');
INSERT INTO plant (name, garden_id, count, description) VALUES ('Rosemary', 3, '9', 'Aromatic herb for seasoning');