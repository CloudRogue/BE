BEGIN;

\copy public.eligibility (id, required_onboarding, type, title, example_question, onboarding_description, question, value) FROM '/csvs/pg_eligibility.csv' WITH (FORMAT csv, HEADER true, NULL '');

\copy public.eligibility_option (display_order, eligibility_id, label) FROM '/csvs/pg_eligibility_option.csv' WITH (FORMAT csv, HEADER true, NULL '');

\copy public.announcement_eligibility (announcement_id, eligibility_id, expected_value) FROM '/csvs/pg_announcement_eligibility.csv' WITH (FORMAT csv, HEADER true, NULL '');

\copy public.profile (created_at, deleted_at, updated_at, user_name, user_id, email) FROM '/csvs/pg_profile.csv' WITH (FORMAT csv, HEADER true, NULL '');

\copy public.notification_setting (email_enabled, kakao_enabled, reminder_days_before, send_at_hour, updated_at, user_id) FROM '/csvs/pg_notification_setting.csv' WITH (FORMAT csv, HEADER true, NULL '');

\copy public.eligibility_answer (created_at, eligibility_id, updated_at, user_id, value) FROM '/csvs/pg_eligibility_answer.csv' WITH (FORMAT csv, HEADER true, NULL '');

\copy public.scrap (announcement_id, created_at, user_id) FROM '/csvs/pg_scrap.csv' WITH (FORMAT csv, HEADER true, NULL '');

\copy public.outbound (announcement_id, created_at, user_id) FROM '/csvs/pg_outbound.csv' WITH (FORMAT csv, HEADER true, NULL '');

COMMIT;

-- Quick checks
SELECT 'eligibility' AS table_name, COUNT(*) AS cnt FROM public.eligibility
UNION ALL SELECT 'eligibility_option', COUNT(*) FROM public.eligibility_option
UNION ALL SELECT 'announcement_eligibility', COUNT(*) FROM public.announcement_eligibility
UNION ALL SELECT 'profile', COUNT(*) FROM public.profile
UNION ALL SELECT 'notification_setting', COUNT(*) FROM public.notification_setting
UNION ALL SELECT 'eligibility_answer', COUNT(*) FROM public.eligibility_answer
UNION ALL SELECT 'scrap', COUNT(*) FROM public.scrap
UNION ALL SELECT 'outbound', COUNT(*) FROM public.outbound;