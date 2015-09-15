ALTER TABLE cart ADD COLUMN referral_discount bigint(20) NOT NULL;
ALTER TABLE cart ADD COLUMN referral_url varchar(255) DEFAULT NULL;
ALTER TABLE cart ADD COLUMN shared bit(1) NOT NULL;
ALTER TABLE cart ADD COLUMN referral bigint(20) DEFAULT NULL;
ALTER TABLE event ADD COLUMN social_sharing_discount_amount bigint(20) NOT NULL;
ALTER TABLE event ADD COLUMN social_sharing_discounts bit(1) NOT NULL;
ALTER TABLE event ADD COLUMN top_sharer_reward varchar(255) DEFAULT NULL;
ALTER TABLE cart  ADD CONSTRAINT fk_cart_referral FOREIGN KEY (referral) REFERENCES cart(id);
