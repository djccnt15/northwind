INSERT INTO supported_lang (lang)
VALUES ('en'), ('ko')
;

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
    ('admin', '$2a$10$svEXU2WX/5cXmDmhgIAsReoeFICRImzhIOASTIoH394bHEFfZwlt6', 1, 1),
    ('purchaser', '$2a$10$svEXU2WX/5cXmDmhgIAsReoeFICRImzhIOASTIoH394bHEFfZwlt6', 1, 1)
;

INSERT INTO app_user_role (app_user_id, user_role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 7)  -- purchaser -> PURCHASE
;

INSERT INTO product_category (name, code, description, created_by, last_modified_by)
VALUES ('Beverages', 'BEV', 'Soft drinks and teas', 1, 1);

INSERT INTO product (code, name, standard_unit_cost, unit_price, reorder_level, target_level,
    quantity_per_unit, minimum_reorder_quantity, discontinued, product_category, created_by, last_modified_by)
VALUES ('P001', 'Chai', 10.00, 18.00, 10, 40, 10, 20, false, 1, 1, 1);

-- tax status
INSERT INTO tax_status (status, created_by, last_modified_by)
VALUES ('Taxable', 1, 1), ('Tax Exempt', 1, 1);

-- company type
INSERT INTO company_type (company_type, created_by, last_modified_by)
VALUES ('Customer', 1, 1), ('Supplier', 1, 1);

-- companies (1: customer, 2: shipper/supplier)
INSERT INTO company (name, company_type_id, tax_status_id, created_by, last_modified_by)
VALUES
    ('Acme Corp', 1, 1, 1, 1),
    ('Fast Shipping Inc', 2, 1, 1, 1);

-- order status master (id order = sort order: PENDING..CANCELLED)
INSERT INTO order_status (code, name, sort_order, created_by, last_modified_by)
VALUES
    ('PENDING', '접수', 'ASC', 1, 1),
    ('PAID', '결제완료', 'ASC', 1, 1),
    ('SHIPPED', '출고', 'ASC', 1, 1),
    ('DELIVERED', '배송완료', 'ASC', 1, 1),
    ('CANCELLED', '취소', 'ASC', 1, 1);

-- order detail status master (id order: 대기, 출고, 취소)
INSERT INTO order_detail_status (name, sort_order, created_by, last_modified_by)
VALUES
    ('대기', 'ASC', 1, 1),
    ('출고', 'ASC', 1, 1),
    ('취소', 'ASC', 1, 1);

-- sample order with one item (order id 1, detail id 1)
INSERT INTO orders (order_date, shipping_fee, customer_id, shipper_id, tax_status_id, status_id, app_user_id,
    created_by, last_modified_by)
VALUES ('2026-06-01', 100, 1, 2, 1, 1, 1, 1, 1);

INSERT INTO order_detail (quantity, unit_price, standard_unit_cost, discount, product_id, order_id,
    order_detail_status_id, created_by, last_modified_by)
VALUES (2, 18.00, 10.00, 0, 1, 1, 1, 1, 1);

-- employee linked to app_user 3 (purchaser), used as submittedBy/approvedBy for purchase orders.
-- (app_user 1/system intentionally has no employee record; EmployeeServiceTest depends on that)
INSERT INTO employee (first_name, last_name, hire_date, title_id, app_user_id, created_by, last_modified_by)
VALUES ('Purchase', 'Manager', '2026-01-01', 1, 3, 1, 1);

-- purchase order status master (id order = sort order: DRAFT..PAID, then REJECTED)
INSERT INTO purchase_order_status (code, name, sort_order, created_by, last_modified_by)
VALUES
    ('DRAFT', '작성중', 'ASC', 1, 1),
    ('PENDING_APPROVAL', '승인대기', 'ASC', 1, 1),
    ('APPROVED', '승인완료', 'ASC', 1, 1),
    ('RECEIVED', '수령완료', 'ASC', 1, 1),
    ('PAID', '대금지급완료', 'ASC', 1, 1),
    ('REJECTED', '반려', 'ASC', 1, 1);

-- sample purchase order with one item (purchase order id 1, detail id 1)
-- vendor = company 2 (Fast Shipping Inc, Supplier), submittedBy = employee 1, status = DRAFT
INSERT INTO purchase_orders (submitted_date, shipping_fee, vendor_id, submitted_by, status_id,
    created_by, last_modified_by)
VALUES ('2026-06-01', 100, 2, 1, 1, 1, 1);

INSERT INTO purchase_order_detail (quantity, unit_price, product_id, purchase_order_id,
    created_by, last_modified_by)
VALUES (5, 10.00, 1, 1, 1, 1);
