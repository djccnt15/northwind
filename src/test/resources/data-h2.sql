INSERT INTO user_role (name)
VALUES
    ('SUPERADMIN'),
    ('ADMIN'),
    ('MANAGER'),
    ('USER')
;

INSERT INTO team (name)
VALUES ('system')
;

INSERT INTO title (title)
VALUES ('system')
;

INSERT INTO app_user (username, password, is_verified, team_id)
VALUES
    ('system', '$2a$10$1MR6S9axJs5wPWz2/O1lKOUFOBQzHqam3qhSEEoaD85c2RxyW6u9W', 1, 1),
    ('admin', '$2a$10$svEXU2WX/5cXmDmhgIAsReoeFICRImzhIOASTIoH394bHEFfZwlt6', 1, 1)
;

INSERT INTO app_user_role (app_user_id, user_role_id)
VALUES
    (1, 1),
    (2, 2)
;
