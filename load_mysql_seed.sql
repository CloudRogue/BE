SET foreign_key_checks = 0;

-- 1) users
LOAD DATA LOCAL INFILE '/csvs/mysql_users.csv'
    INTO TABLE users
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (provider, id, user_id, name, provider_user_id, email, role);

-- 2) announcements (FK 부모)
LOAD DATA LOCAL INFILE '/csvs/announcements.csv'
    INTO TABLE announcements
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (
     admin_checked, document_published_at, end_date, final_published_at, start_date,
     created_at, enty, id, mt_rntchrg, prtpay, rent_gtn, surlus, updated_at,
     apply_entry_url, apply_url, external_key, full_address, housing_type, publisher,
     refrn_legaldong_nm, region_code, region_name, supply_type, title, source
        );

-- 3) overviews
LOAD DATA LOCAL INFILE '/csvs/announcement_overviews.csv'
    INTO TABLE announcement_overviews
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (announcement_id, created_at, overview_id, updated_at, apply_method, content, target);

-- 4) summaries
LOAD DATA LOCAL INFILE '/csvs/announcement_summaries.csv'
    INTO TABLE announcement_summaries
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (announcement_id, created_at, summary_id, updated_at, summary);

-- 5) regions (FK)
LOAD DATA LOCAL INFILE '/csvs/announcement_regions.csv'
    INTO TABLE announcement_regions
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (announcement_id, created_at, region_id, region_name);

-- 6) documents (FK)
LOAD DATA LOCAL INFILE '/csvs/announcement_documents.csv'
    INTO TABLE announcement_documents
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (announcement_id, created_at, id, name, phase, scope);

-- 7) outbound_logs (FK)
LOAD DATA LOCAL INFILE '/csvs/announcement_outbound_logs.csv'
    INTO TABLE announcement_outbound_logs
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (announcement_id, created_at, outbound_id, destination_url, user_id);

-- 8) announcement_applications (UNIQUE(user_id, announcement_id))
LOAD DATA LOCAL INFILE '/csvs/mysql_announcement_applications.csv'
    INTO TABLE announcement_applications
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ',' ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (announcement_id, created_at, id, user_id);

SET foreign_key_checks = 1;

-- -----------------------------
-- AUTO_INCREMENT 보정(선택)
-- -----------------------------
SET @mx_user_id     = (SELECT IFNULL(MAX(id),0) FROM users);
SET @mx_region_id   = (SELECT IFNULL(MAX(region_id),0) FROM announcement_regions);
SET @mx_doc_id      = (SELECT IFNULL(MAX(id),0) FROM announcement_documents);
SET @mx_summary_id  = (SELECT IFNULL(MAX(summary_id),0) FROM announcement_summaries);
SET @mx_overview_id = (SELECT IFNULL(MAX(overview_id),0) FROM announcement_overviews);
SET @mx_outbound_id = (SELECT IFNULL(MAX(outbound_id),0) FROM announcement_outbound_logs);
SET @mx_app_id      = (SELECT IFNULL(MAX(id),0) FROM announcement_applications);

SET @sql = CONCAT('ALTER TABLE users AUTO_INCREMENT = ', @mx_user_id + 1);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql = CONCAT('ALTER TABLE announcement_regions AUTO_INCREMENT = ', @mx_region_id + 1);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql = CONCAT('ALTER TABLE announcement_documents AUTO_INCREMENT = ', @mx_doc_id + 1);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql = CONCAT('ALTER TABLE announcement_summaries AUTO_INCREMENT = ', @mx_summary_id + 1);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql = CONCAT('ALTER TABLE announcement_overviews AUTO_INCREMENT = ', @mx_overview_id + 1);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql = CONCAT('ALTER TABLE announcement_outbound_logs AUTO_INCREMENT = ', @mx_outbound_id + 1);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql = CONCAT('ALTER TABLE announcement_applications AUTO_INCREMENT = ', @mx_app_id + 1);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Quick checks
SELECT 'users' AS table_name, COUNT(*) AS cnt FROM users
UNION ALL SELECT 'announcements', COUNT(*) FROM announcements
UNION ALL SELECT 'announcement_regions', COUNT(*) FROM announcement_regions
UNION ALL SELECT 'announcement_documents', COUNT(*) FROM announcement_documents
UNION ALL SELECT 'announcement_summaries', COUNT(*) FROM announcement_summaries
UNION ALL SELECT 'announcement_overviews', COUNT(*) FROM announcement_overviews
UNION ALL SELECT 'announcement_outbound_logs', COUNT(*) FROM announcement_outbound_logs
UNION ALL SELECT 'announcement_applications', COUNT(*) FROM announcement_applications;