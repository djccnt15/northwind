INSERT INTO user_role (name)
VALUES
    ('SUPERADMIN'), ('ADMIN'), ('MANAGER'), ('USER'),
    ('COMPANY'), ('ORDER'), ('PURCHASE'), ('PRODUCT'), ('STOCK')
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

INSERT INTO product_category (name, code, description, created_by, last_modified_by)
VALUES ('Beverages', 'BEV', 'Soft drinks and teas', 1, 1);

INSERT INTO product (code, name, standard_unit_cost, unit_price, reorder_level, target_level,
    quantity_per_unit, minimum_reorder_quantity, discontinued, product_category, created_by, last_modified_by)
VALUES ('P001', 'Chai', 10.00, 18.00, 10, 40, 10, 20, false, 1, 1, 1);
