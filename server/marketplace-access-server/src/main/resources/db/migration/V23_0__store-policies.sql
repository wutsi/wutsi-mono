ALTER TABLE T_STORE ADD COLUMN cancellation_accepted BOOLEAN DEFAULT false;
ALTER TABLE T_STORE ADD COLUMN cancellation_window INT DEFAULT 12;
ALTER TABLE T_STORE ADD COLUMN cancellation_message TEXT;

ALTER TABLE T_STORE ADD COLUMN return_accepted BOOLEAN DEFAULT false;
ALTER TABLE T_STORE ADD COLUMN return_contact_window INT DEFAULT 48;
ALTER TABLE T_STORE ADD COLUMN return_ship_back_window INT DEFAULT 240;
ALTER TABLE T_STORE ADD COLUMN return_message TEXT;
