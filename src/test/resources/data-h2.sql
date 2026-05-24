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

INSERT INTO app_user (username, password, team_id)
VALUES
    ('system', 'system', 1),
    ('admin', 'admin', 1)
;

INSERT INTO app_user_role (app_user_id, user_role_id)
VALUES
    (1, 1),
    (2, 2)
;
