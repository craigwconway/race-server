ALTER TABLE custom_reg_field CHANGE COLUMN responses responses VARCHAR(2000);
ALTER TABLE custom_reg_field ADD COLUMN hint VARCHAR(255);
