-- Stored Procedure untuk Laporan Aktivitas Zakat
DROP PROCEDURE IF EXISTS `sp_laporan_dana_zakat`;
CREATE DEFINER=`lazisdbuser`@`%` PROCEDURE `sp_laporan_dana_zakat`(
    IN p_bulan1 VARCHAR(7),
    IN p_bulan2 VARCHAR(7)
)
BEGIN
    WITH params AS (
        SELECT p_bulan1 AS bulan1, p_bulan2 AS bulan2
    ),
    semua_bulan AS (
        SELECT DISTINCT DATE_FORMAT(transaction_date, '%Y-%m') AS bulan
        FROM transaction
        WHERE category = 'zakat'
        UNION SELECT bulan1 FROM params
        UNION SELECT bulan2 FROM params
    ),
    transaksi_all AS (
        SELECT
            DATE_FORMAT(t.transaction_date, '%Y-%m') AS bulan,
            t.coa_id,
            t.penyaluran,
            t.kredit,
            t.debit
        FROM transaction t
        WHERE t.category = 'zakat' AND t.category <> 'hasil bagi bank'
    ),
    penerimaan AS (
        SELECT
            bulan,
            SUM(CASE WHEN coa_id = 53 THEN kredit ELSE 0 END) AS zakat_maal,
            SUM(CASE WHEN coa_id = 54 THEN kredit ELSE 0 END) AS zakat_fitrah,
            SUM(CASE WHEN coa_id = 55 THEN kredit ELSE 0 END) AS fidyah
        FROM transaksi_all
        WHERE penyaluran = 0
        GROUP BY bulan
    ),
    pendayagunaan AS (
        SELECT
            bulan,
            SUM(CASE WHEN coa_id IN (57, 64, 67, 68) THEN debit ELSE 0 END) AS fakir,
            SUM(CASE WHEN coa_id = 70 THEN debit ELSE 0 END) AS miskin,
            SUM(CASE WHEN coa_id = 62 THEN debit ELSE 0 END) AS amil,
            SUM(CASE WHEN coa_id = 59 THEN debit ELSE 0 END) AS muallaf,
            SUM(CASE WHEN coa_id IN (58, 60, 61, 63, 66, 65) THEN debit ELSE 0 END) AS fiisabilillah,
            SUM(CASE WHEN coa_id = 69 THEN debit ELSE 0 END) AS ibnu_sabil
        FROM transaksi_all
        WHERE penyaluran = 1
        GROUP BY bulan
    ),
    laporan_base AS (
        SELECT
            sb.bulan,
            COALESCE(p.zakat_maal, 0) AS zakat_maal,
            COALESCE(p.zakat_fitrah, 0) AS zakat_fitrah,
            COALESCE(p.fidyah, 0) AS fidyah,
            COALESCE(d.fakir, 0) AS fakir,
            COALESCE(d.miskin, 0) AS miskin,
            COALESCE(d.amil, 0) AS amil,
            COALESCE(d.muallaf, 0) AS muallaf,
            COALESCE(d.fiisabilillah, 0) AS fiisabilillah,
            COALESCE(d.ibnu_sabil, 0) AS ibnu_sabil,
            (COALESCE(p.zakat_maal, 0) + COALESCE(p.zakat_fitrah, 0) + COALESCE(p.fidyah, 0)) AS total_penerimaan,
            (COALESCE(d.fakir, 0) + COALESCE(d.miskin, 0) + COALESCE(d.amil, 0) + COALESCE(d.muallaf, 0) + COALESCE(d.fiisabilillah, 0) + COALESCE(d.ibnu_sabil, 0)) AS total_pendayagunaan
        FROM semua_bulan sb
        LEFT JOIN penerimaan p ON sb.bulan = p.bulan
        LEFT JOIN pendayagunaan d ON sb.bulan = d.bulan
    ),
    saldo_awal_input AS (
        SELECT COALESCE((SELECT saldo_awal FROM saldo_awal WHERE coa_id = 45 ORDER BY tanggal_input LIMIT 1), 0) AS nilai_statis
    ),
    saldo_berantai AS (
        SELECT
            l.*,
            (sa.nilai_statis + COALESCE(SUM(l.total_penerimaan - l.total_pendayagunaan) OVER (ORDER BY l.bulan ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING), 0)) AS s_awal,
            (sa.nilai_statis + SUM(l.total_penerimaan - l.total_pendayagunaan) OVER (ORDER BY l.bulan)) AS s_akhir
        FROM laporan_base l
        CROSS JOIN saldo_awal_input sa
    )
    SELECT uraian, bulan1, bulan2 FROM (
        SELECT 'Penerimaan Dana Zakat Maal' AS uraian, 1 AS urut,
        MAX(CASE WHEN bulan = p.bulan1 THEN zakat_maal ELSE 0 END) AS bulan1,
        MAX(CASE WHEN bulan = p.bulan2 THEN zakat_maal ELSE 0 END) AS bulan2 FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Penerimaan Dana Zakat Fitrah', 2,
        MAX(CASE WHEN bulan = p.bulan1 THEN zakat_fitrah ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN zakat_fitrah ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Penerimaan Dana Fidyah', 3,
        MAX(CASE WHEN bulan = p.bulan1 THEN fidyah ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN fidyah ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Jumlah Penerimaan Dana Zakat', 4,
        MAX(CASE WHEN bulan = p.bulan1 THEN total_penerimaan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN total_penerimaan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Fakir', 5,
        MAX(CASE WHEN bulan = p.bulan1 THEN fakir ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN fakir ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Miskin', 6,
        MAX(CASE WHEN bulan = p.bulan1 THEN miskin ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN miskin ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Amil', 7,
        MAX(CASE WHEN bulan = p.bulan1 THEN amil ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN amil ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Muallaf', 8,
        MAX(CASE WHEN bulan = p.bulan1 THEN muallaf ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN muallaf ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Fiisabilillah', 9,
        MAX(CASE WHEN bulan = p.bulan1 THEN fiisabilillah ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN fiisabilillah ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Ibnu Sabil', 10,
        MAX(CASE WHEN bulan = p.bulan1 THEN ibnu_sabil ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN ibnu_sabil ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Jumlah Pendayagunaan Dana Zakat', 11,
        MAX(CASE WHEN bulan = p.bulan1 THEN total_pendayagunaan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN total_pendayagunaan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Surplus (Defisit) Dana Zakat', 12,
        MAX(CASE WHEN bulan = p.bulan1 THEN (total_penerimaan - total_pendayagunaan) ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN (total_penerimaan - total_pendayagunaan) ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Saldo Awal Dana Zakat', 13,
        MAX(CASE WHEN bulan = p.bulan1 THEN s_awal ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN s_awal ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Saldo Akhir Dana Zakat', 14,
        MAX(CASE WHEN bulan = p.bulan1 THEN s_akhir ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN s_akhir ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
    ) final_result
    ORDER BY urut;
END;

-- Stored Procedure untuk Laporan Aktivitas DSKL
DROP PROCEDURE IF EXISTS `sp_laporan_dana_dskl`;
CREATE DEFINER=`lazisdbuser`@`%` PROCEDURE `sp_laporan_dana_dskl`(
    IN p_bulan1 VARCHAR(7),
    IN p_bulan2 VARCHAR(7)
)
BEGIN
    WITH params AS (
        SELECT p_bulan1 AS bulan1, p_bulan2 AS bulan2
    ),
    semua_bulan AS (
        SELECT DISTINCT DATE_FORMAT(transaction_date, '%Y-%m') AS bulan
        FROM transaction
        WHERE category IN ('dskl', 'zakat') -- Menambahkan zakat untuk Fidyah
        UNION SELECT bulan1 FROM params
        UNION SELECT bulan2 FROM params
    ),
    transaksi_all AS (
        SELECT
            DATE_FORMAT(t.transaction_date, '%Y-%m') AS bulan,
            t.coa_id,
            t.penyaluran,
            t.kredit,
            t.debit,
            t.category
        FROM transaction t
        WHERE t.category IN ('dskl', 'zakat') AND t.category <> 'hasil bagi bank'
    ),
    penerimaan AS (
        SELECT
            bulan,
            SUM(CASE WHEN coa_id IN (SELECT id FROM coa WHERE parent_account_id = 98) THEN kredit ELSE 0 END) AS dana_dskl,
            SUM(CASE WHEN coa_id = 55 THEN kredit ELSE 0 END) AS dana_fidyah,
            0 AS dana_qurban -- Placeholder untuk Qurban
        FROM transaksi_all
        WHERE penyaluran = 0
        GROUP BY bulan
    ),
    pendayagunaan AS (
        SELECT
            bulan,
            SUM(CASE WHEN coa_id IN (103, 104, 105) THEN debit ELSE 0 END) AS dakwah,
            SUM(CASE WHEN coa_id IN (106, 107, 108, 109, 110, 111, 112, 113) THEN debit ELSE 0 END) AS pendidikan,
            SUM(CASE WHEN coa_id IN (114, 115) THEN debit ELSE 0 END) AS kesehatan
        FROM transaksi_all
        WHERE penyaluran = 1 AND category = 'dskl'
        GROUP BY bulan
    ),
    laporan_base AS (
        SELECT
            sb.bulan,
            COALESCE(p.dana_dskl, 0) AS dana_dskl,
            COALESCE(p.dana_fidyah, 0) AS dana_fidyah,
            COALESCE(p.dana_qurban, 0) AS dana_qurban,
            COALESCE(d.dakwah, 0) AS dakwah,
            COALESCE(d.pendidikan, 0) AS pendidikan,
            COALESCE(d.kesehatan, 0) AS kesehatan,
            (COALESCE(p.dana_dskl, 0) + COALESCE(p.dana_fidyah, 0) + COALESCE(p.dana_qurban, 0)) AS total_penerimaan,
            (COALESCE(d.dakwah, 0) + COALESCE(d.pendidikan, 0) + COALESCE(d.kesehatan, 0)) AS total_pendayagunaan
        FROM semua_bulan sb
        LEFT JOIN penerimaan p ON sb.bulan = p.bulan
        LEFT JOIN pendayagunaan d ON sb.bulan = d.bulan
    ),
    saldo_awal_input AS (
        SELECT COALESCE((SELECT saldo_awal FROM saldo_awal WHERE coa_id = 47 ORDER BY tanggal_input LIMIT 1), 0) AS nilai_statis
    ),
    saldo_berantai AS (
        SELECT
            l.*,
            (sa.nilai_statis + COALESCE(SUM(l.total_penerimaan - l.total_pendayagunaan) OVER (ORDER BY l.bulan ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING), 0)) AS s_awal,
            (sa.nilai_statis + SUM(l.total_penerimaan - l.total_pendayagunaan) OVER (ORDER BY l.bulan)) AS s_akhir
        FROM laporan_base l
        CROSS JOIN saldo_awal_input sa
    )
    SELECT uraian, bulan1, bulan2 FROM (
        SELECT 'Penerimaan Dana DSKL' AS uraian, 1 AS urut,
        MAX(CASE WHEN bulan = p.bulan1 THEN dana_dskl ELSE 0 END) AS bulan1,
        MAX(CASE WHEN bulan = p.bulan2 THEN dana_dskl ELSE 0 END) AS bulan2 FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Penerimaan Dana Fidyah', 2,
        MAX(CASE WHEN bulan = p.bulan1 THEN dana_fidyah ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN dana_fidyah ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Penerimaan Dana Qurban', 3,
        MAX(CASE WHEN bulan = p.bulan1 THEN dana_qurban ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN dana_qurban ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Jumlah Penerimaan Dana DSKL', 4,
        MAX(CASE WHEN bulan = p.bulan1 THEN total_penerimaan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN total_penerimaan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Dakwah', 5,
        MAX(CASE WHEN bulan = p.bulan1 THEN dakwah ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN dakwah ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Pendidikan', 6,
        MAX(CASE WHEN bulan = p.bulan1 THEN pendidikan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN pendidikan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Kesehatan', 7,
        MAX(CASE WHEN bulan = p.bulan1 THEN kesehatan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN kesehatan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Jumlah Pendayagunaan Dana DSKL', 8,
        MAX(CASE WHEN bulan = p.bulan1 THEN total_pendayagunaan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN total_pendayagunaan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Surplus (Defisit) Dana DSKL', 9,
        MAX(CASE WHEN bulan = p.bulan1 THEN (total_penerimaan - total_pendayagunaan) ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN (total_penerimaan - total_pendayagunaan) ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Saldo Awal Dana DSKL', 10,
        MAX(CASE WHEN bulan = p.bulan1 THEN s_awal ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN s_awal ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Saldo Akhir Dana DSKL', 11,
        MAX(CASE WHEN bulan = p.bulan1 THEN s_akhir ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN s_akhir ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
    ) final_result
    ORDER BY urut;
END;

-- Stored Procedure untuk Laporan Aktivitas Pengelola
DROP PROCEDURE IF EXISTS `sp_laporan_dana_pengelola`;
CREATE DEFINER=`lazisdbuser`@`%` PROCEDURE `sp_laporan_dana_pengelola`(
    IN p_bulan1 VARCHAR(7),
    IN p_bulan2 VARCHAR(7)
)
BEGIN
    WITH params AS (
        SELECT p_bulan1 AS bulan1, p_bulan2 AS bulan2
    ),
    semua_bulan AS (
        SELECT DISTINCT DATE_FORMAT(transaction_date, '%Y-%m') AS bulan
        FROM transaction
        WHERE category IN ('zakat', 'infak', 'campaign', 'dskl', 'hasil bagi bank')
        UNION SELECT bulan1 FROM params
        UNION SELECT bulan2 FROM params
    ),
    penerimaan_sumber AS (
        SELECT
            DATE_FORMAT(transaction_date, '%Y-%m') AS bulan,
            SUM(CASE WHEN category = 'zakat' THEN kredit ELSE 0 END) AS total_zakat,
            SUM(CASE WHEN category IN ('infak', 'campaign') THEN kredit ELSE 0 END) AS total_infak,
            SUM(CASE WHEN category = 'dskl' THEN kredit ELSE 0 END) AS total_dskl,
            SUM(CASE WHEN coa_id = 120 THEN kredit ELSE 0 END) AS bagi_hasil
        FROM transaction
        WHERE penyaluran = 0
        GROUP BY bulan
    ),
    penerimaan_pengelola AS (
        SELECT
            bulan,
            (total_zakat * 0.125) AS dana_amil_zakat,
            ((total_infak + total_dskl) * 0.20) AS dana_amil_lain,
            bagi_hasil
        FROM penerimaan_sumber
    ),
    pendayagunaan_per_coa AS (
        SELECT
            DATE_FORMAT(t.transaction_date, '%Y-%m') AS bulan,
            c.account_name,
            SUM(t.debit) AS total_debit
        FROM transaction t
        JOIN coa c ON t.coa_id = c.id
        WHERE t.penyaluran = 1 AND c.parent_account_id = 121
        GROUP BY bulan, c.account_name
    ),
    total_pendayagunaan_pengelola AS (
        SELECT bulan, SUM(total_debit) AS total_all
        FROM pendayagunaan_per_coa
        GROUP BY bulan
    ),
    laporan_base AS (
        SELECT
            sb.bulan,
            COALESCE(pp.dana_amil_zakat, 0) AS dana_amil_zakat,
            COALESCE(pp.dana_amil_lain, 0) AS dana_amil_lain,
            COALESCE(pp.bagi_hasil, 0) AS bagi_hasil,
            (COALESCE(pp.dana_amil_zakat, 0) + COALESCE(pp.dana_amil_lain, 0) + COALESCE(pp.bagi_hasil, 0)) AS total_penerimaan,
            COALESCE(pd.total_all, 0) AS total_pendayagunaan
        FROM semua_bulan sb
        LEFT JOIN penerimaan_pengelola pp ON sb.bulan = pp.bulan
        LEFT JOIN total_pendayagunaan_pengelola pd ON sb.bulan = pd.bulan
    ),
    saldo_awal_input AS (
        SELECT COALESCE((SELECT saldo_awal FROM saldo_awal WHERE coa_id = 50 ORDER BY tanggal_input LIMIT 1), 0) AS nilai_statis
    ),
    saldo_berantai AS (
        SELECT
            l.*,
            (sa.nilai_statis + COALESCE(SUM(l.total_penerimaan - l.total_pendayagunaan) OVER (ORDER BY l.bulan ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING), 0)) AS s_awal,
            (sa.nilai_statis + SUM(l.total_penerimaan - l.total_pendayagunaan) OVER (ORDER BY l.bulan)) AS s_akhir
        FROM laporan_base l
        CROSS JOIN saldo_awal_input sa
    )
    SELECT uraian, bulan1, bulan2 FROM (
        -- PENERIMAAN
        SELECT 'Penerimaan Dana Amil Zakat' AS uraian, 1 AS urut,
        MAX(CASE WHEN bulan = p.bulan1 THEN dana_amil_zakat ELSE 0 END) AS bulan1,
        MAX(CASE WHEN bulan = p.bulan2 THEN dana_amil_zakat ELSE 0 END) AS bulan2 FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Penerimaan Dana Amil (Infak, DSKL, Campaign)', 2,
        MAX(CASE WHEN bulan = p.bulan1 THEN dana_amil_lain ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN dana_amil_lain ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Penerimaan Bagi Hasil Bank', 3,
        MAX(CASE WHEN bulan = p.bulan1 THEN bagi_hasil ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN bagi_hasil ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Jumlah Penerimaan Dana Pengelola', 4,
        MAX(CASE WHEN bulan = p.bulan1 THEN total_penerimaan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN total_penerimaan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        
        -- PENDAYAGUNAAN (DINAMIS DARI COA)
        UNION ALL
        SELECT c.account_name, 5,
        COALESCE(MAX(CASE WHEN t.bulan = p.bulan1 THEN t.total_debit ELSE 0 END), 0),
        COALESCE(MAX(CASE WHEN t.bulan = p.bulan2 THEN t.total_debit ELSE 0 END), 0)
        FROM coa c
        CROSS JOIN params p
        LEFT JOIN pendayagunaan_per_coa t ON c.account_name = t.account_name
        WHERE c.parent_account_id = 121
        GROUP BY c.account_name
        
        UNION ALL
        SELECT 'Jumlah Pendayagunaan Dana Pengelola', 6,
        MAX(CASE WHEN bulan = p.bulan1 THEN total_pendayagunaan ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN total_pendayagunaan ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        
        -- SURPLUS & SALDO
        UNION ALL
        SELECT 'Surplus (Defisit) Dana Pengelola', 12,
        MAX(CASE WHEN bulan = p.bulan1 THEN (total_penerimaan - total_pendayagunaan) ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN (total_penerimaan - total_pendayagunaan) ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Saldo Awal Dana Pengelola', 13,
        MAX(CASE WHEN bulan = p.bulan1 THEN s_awal ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN s_awal ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
        UNION ALL
        SELECT 'Saldo Akhir Dana Pengelola', 14,
        MAX(CASE WHEN bulan = p.bulan1 THEN s_akhir ELSE 0 END),
        MAX(CASE WHEN bulan = p.bulan2 THEN s_akhir ELSE 0 END) FROM saldo_berantai CROSS JOIN params p
    ) final_result
    ORDER BY urut, uraian;
END;
