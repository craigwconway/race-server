ALTER TABLE custom_reg_field ADD COLUMN responses VARCHAR(255);
ALTER TABLE event_cart_item ADD COLUMN shirts_limited bit(1) NOT NULL;
ALTER TABLE event ADD COLUMN shirts_limited bit(1) NOT NULL;
ALTER TABLE cart ADD COLUMN questions bigint(20) NOT NULL;
