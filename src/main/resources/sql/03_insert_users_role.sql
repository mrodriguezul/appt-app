-- Inserts para la tabla users_role
-- Asigna roles a los usuarios existentes

-- Asignar rol ADMIN al usuario admin
insert into users_role (username, role, granted_date) values
('admin', 'ADMIN', '2025-09-12 10:00:00');

-- Asignar rol ASSISTANT al usuario assistant
insert into users_role (username, role, granted_date) values
('assistant', 'ASSISTANT', '2025-09-12 10:00:00');
