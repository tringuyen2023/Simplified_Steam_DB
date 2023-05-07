create database Steam;
use Steam;

CREATE TABLE Users (
	user_id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
	username varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	dob DATE NOT NULL,
	country varchar(255) NOT NULL
);

CREATE TABLE Games (
	game_id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
	title varchar(255) NOT NULL,
	developer varchar(255) NOT NULL,
	release_date DATE NOT NULL,
	price FLOAT NOT NULL,
	genre varchar(255) NOT NULL
);

CREATE TABLE Libraries (
	user_id int NOT NULL,
	game_id int NOT NULL,
	play_time int NOT NULL,
	date_purchased DATE NOT NULL,
	FOREIGN KEY (game_id) REFERENCES Games(game_id),
	FOREIGN KEY (user_id) REFERENCES Users(user_id)
);



-- Insert data into Users table
INSERT INTO Users (username, email, dob, country) VALUES
('john doe', 'john.doe@example.com', '1990-01-01', 'USA'),
('jane smith', 'jane.smith@example.com', '1995-03-15', 'Canada'),
('bob jones', 'bob.jones@example.com', '1985-07-22', 'UK'),
('susan lee', 'susan.lee@example.com', '1998-09-12', 'South Korea'),
('jimmy nguyen', 'jimmy.nguyen@example.com', '1982-12-31', 'Vietnam'),
('lisa tan', 'lisa.tan@example.com', '1993-05-17', 'Singapore'),
('michael wong', 'michael.wong@example.com', '1980-11-03', 'Hong Kong'),
('jessica kim', 'jessica.kim@example.com', '1996-04-23', 'South Korea'),
('david liu', 'david.liu@example.com', '1987-08-10', 'China'),
('ashley wu', 'ashley.wu@example.com', '1991-02-14', 'Taiwan');

INSERT INTO Games (title, developer, release_date, price, genre) VALUES
('Super Mario Bros.', 'Nintendo', '1985-09-13', 19.99, 'Platformer'),
('The Legend of Zelda', 'Nintendo', '1986-02-21', 29.99, 'Action-Adventure'),
('Tetris', 'Tetris Company', '1984-06-06', 9.99, 'Puzzle'),
('Final Fantasy VII', 'Square Enix', '1997-01-31', 59.99, 'Role-playing'),
('Grand Theft Auto V', 'Rockstar Games', '2013-09-17', 49.99, 'Action-adventure'),
('Minecraft', 'Mojang Studios', '2011-11-18', 26.95, 'Sandbox'),
('Overwatch', 'Blizzard Entertainment', '2016-05-24', 39.99, 'First-person shooter'),
('The Witcher 3: Wild Hunt', 'CD Projekt Red', '2015-05-19', 39.99, 'Action role-playing'),
('Pokémon Red and Blue', 'Game Freak', '1996-02-27', 19.99, 'Role-playing'),
('Counter-Strike: Global Offensive', 'Valve Corporation', '2012-08-21', 14.99, 'First-person shooter');

-- Insert data into Libraries table
INSERT INTO Libraries (user_id, game_id, play_time, date_purchased) VALUES
(1, 4, 119, '2023-05-02'),
(2, 6, 300, '2009-05-03'),
(3, 1, 1000, '2019-05-02'),
(4, 8, 42, '2023-05-01'),
(5, 9, 729, '2012-05-03'),
(6, 7, 0, '2023-05-02'),
(7, 2, 137, '2018-05-01'),
(8, 5, 0, '2016-05-03'),
(9, 3, 72349, '2020-05-02'),
(10, 10, 100, '2023-05-03');

-- SELECT * FROM Users;
-- SELECT * FROM Libraries;
-- SELECT * FROM Games;

-- DROP TABLE Libraries;
-- DROP TABLE Users;