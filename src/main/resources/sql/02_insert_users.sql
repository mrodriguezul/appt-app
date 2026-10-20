-- user admin password: qwerty123
-- user assistant password: qwerty1234
-- Use to generate hash: https://bcrypt.online/

insert into users (username, password, email, locked, disabled) values
('admin', '$2y$08$BeblEHN5OZNuJtkqlQTaWeVXyCflCTyznunFair1N1d0AN9DAeGVC', 'admin@email.com', false, false),
('assistant', '$2y$08$Mq57CwysOz6MxG13jDq19eh1XZuMwm1uXpfGt0lTiyz.P89h6eICy', 'assistant@email.com', false, false);

