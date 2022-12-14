DROP TABLE IF EXISTS warehouse;
CREATE TABLE IF NOT EXISTS warehouse
(
    W_ID
    int,
    W_NAME
    varchar
(
    10
),
    W_STREET_1 varchar
(
    20
),
    W_STREET_2 varchar
(
    20
),
    W_CITY varchar
(
    20
),
    W_STATE char
(
    2
),
    W_ZIP char
(
    9
),
    W_TAX decimal
(
    4,
    4
),
    W_YTD decimal
(
    12,
    2
),
    PRIMARY KEY
(
    W_ID
)
    )
PARTITION BY RANGE (W_ID);

CREATE TABLE warehouse01 PARTITION OF warehouse
    FOR VALUES FROM (1) TO (3);

CREATE TABLE warehouse02 PARTITION OF warehouse
    FOR VALUES FROM (3) TO (5);

CREATE TABLE warehouse03 PARTITION OF warehouse
    FOR VALUES FROM (5) TO (7);

CREATE TABLE warehouse04 PARTITION OF warehouse
    FOR VALUES FROM (7) TO (9);

CREATE TABLE warehouse05 PARTITION OF warehouse
    FOR VALUES FROM (9) TO (11);

DROP TABLE IF EXISTS district;
CREATE TABLE IF NOT EXISTS district
(
    D_W_ID
    int,
    D_ID
    int,
    D_NAME
    varchar
(
    10
),
    D_STREET_1 varchar
(
    20
),
    D_STREET_2 varchar
(
    20
),
    D_CITY varchar
(
    20
),
    D_STATE char
(
    2
),
    D_ZIP char
(
    9
),
    D_TAX decimal
(
    4,
    4
),
    D_YTD decimal
(
    12,
    2
),
    D_NEXT_O_ID int,
    D_NEXT_DELIVER_O_ID int,
    PRIMARY KEY
(
    D_W_ID,
    D_ID
)
    )
PARTITION BY RANGE (D_W_ID);

CREATE TABLE district01 PARTITION OF district
    FOR VALUES FROM (1) TO (3);

CREATE TABLE district02 PARTITION OF district
    FOR VALUES FROM (3) TO (5);

CREATE TABLE district03 PARTITION OF district
    FOR VALUES FROM (5) TO (7);

CREATE TABLE district04 PARTITION OF district
    FOR VALUES FROM (7) TO (9);

CREATE TABLE district05 PARTITION OF district
    FOR VALUES FROM (9) TO (11);

DROP TABLE IF EXISTS customer;
CREATE TABLE IF NOT EXISTS customer
(
    C_W_ID
    int,
    C_D_ID
    int,
    C_ID
    int,
    C_FIRST
    varchar
(
    16
),
    C_MIDDLE char
(
    2
),
    C_LAST varchar
(
    16
),
    C_STREET_1 varchar
(
    20
),
    C_STREET_2 varchar
(
    20
),
    C_CITY varchar
(
    20
),
    C_STATE char
(
    2
),
    C_ZIP char
(
    9
),
    C_PHONE char
(
    16
),
    C_SINCE timestamp,
    C_CREDIT char
(
    2
),
    C_CREDIT_LIM decimal
(
    12,
    2
),
    C_DISCOUNT decimal
(
    4,
    4
),
    C_BALANCE decimal
(
    12,
    2
),
    C_YTD_PAYMENT float,
    C_PAYMENT_CNT int,
    C_DELIVERY_CNT int,
    C_DATA varchar
(
    500
),
    PRIMARY KEY
(
    C_W_ID,
    C_D_ID,
    C_ID
)
    )
PARTITION BY RANGE (C_W_ID);

CREATE INDEX c_balance ON customer USING BTREE (C_BALANCE);

CREATE TABLE customer01 PARTITION OF customer
    FOR VALUES FROM (1) TO (3);

CREATE TABLE customer02 PARTITION OF customer
    FOR VALUES FROM (3) TO (5);

CREATE TABLE customer03 PARTITION OF customer
    FOR VALUES FROM (5) TO (7);

CREATE TABLE customer04 PARTITION OF customer
    FOR VALUES FROM (7) TO (9);

CREATE TABLE customer05 PARTITION OF customer
    FOR VALUES FROM (9) TO (11);


DROP TABLE IF EXISTS "order";
CREATE TABLE IF NOT EXISTS "order"
(
    O_W_ID
    int,
    O_D_ID
    int,
    O_ID
    int,
    O_C_ID
    int,
    O_CARRIER_ID
    int,
    O_OL_CNT
    decimal
(
    2,
    0
),
    O_ALL_LOCAL decimal
(
    1,
    0
),
    O_ENTRY_D timestamp,
    PRIMARY KEY
(
    O_W_ID,
    O_D_ID,
    O_ID
)
    )
PARTITION BY RANGE (O_W_ID);


CREATE TABLE "order01" PARTITION OF "order"
    FOR VALUES FROM (1) TO (3);

CREATE TABLE "order02" PARTITION OF "order"
    FOR VALUES FROM (3) TO (5);

CREATE TABLE "order03" PARTITION OF "order"
    FOR VALUES FROM (5) TO (7);

CREATE TABLE "order04" PARTITION OF "order"
    FOR VALUES FROM (7) TO (9);

CREATE TABLE "order05" PARTITION OF "order"
    FOR VALUES FROM (9) TO (11);

DROP TABLE IF EXISTS item;
CREATE TABLE IF NOT EXISTS item
(
    I_ID
    int,
    I_NAME
    varchar,
    I_PRICE
    decimal,
    I_IM_ID
    int,
    I_DATA
    varchar,
    PRIMARY
    KEY
(
    I_ID
)
    );

DROP TABLE IF EXISTS order_line;
CREATE TABLE IF NOT EXISTS order_line
(
    OL_W_ID
    int,
    OL_D_ID
    int,
    OL_O_ID
    int,
    OL_NUMBER
    int,
    OL_I_ID
    int,
    OL_DELIVERY_D
    timestamp,
    OL_AMOUNT
    decimal
(
    8,
    2
),
    OL_SUPPLY_W_ID int,
    OL_QUANTITY decimal
(
    2,
    0
),
    OL_DIST_INFO char
(
    256
),
    OL_C_ID int,
    PRIMARY KEY
(
    OL_W_ID,
    OL_D_ID,
    OL_O_ID,
    OL_NUMBER
)
    )
PARTITION BY RANGE (OL_W_ID);

CREATE TABLE order_line01 PARTITION OF order_line
    FOR VALUES FROM (1) TO (3);

CREATE TABLE order_line02 PARTITION OF order_line
    FOR VALUES FROM (3) TO (5);

CREATE TABLE order_line03 PARTITION OF order_line
    FOR VALUES FROM (5) TO (7);

CREATE TABLE order_line04 PARTITION OF order_line
    FOR VALUES FROM (7) TO (9);

CREATE TABLE order_line05 PARTITION OF order_line
    FOR VALUES FROM (9) TO (11);



DROP TABLE IF EXISTS stock;
CREATE TABLE IF NOT EXISTS stock
(
    S_W_ID
    int,
    S_I_ID
    int,
    S_QUANTITY
    decimal
(
    4,
    0
),
    S_YTD decimal
(
    8,
    2
),
    S_ORDER_CNT int,
    S_REMOTE_CNT int,
    S_DIST_01 char
(
    24
),
    S_DIST_02 char
(
    24
),
    S_DIST_03 char
(
    24
),
    S_DIST_04 char
(
    24
),
    S_DIST_05 char
(
    24
),
    S_DIST_06 char
(
    24
),
    S_DIST_07 char
(
    24
),
    S_DIST_08 char
(
    24
),
    S_DIST_09 char
(
    24
),
    S_DIST_10 char
(
    24
),
    S_DATA varchar
(
    50
),
    PRIMARY KEY
(
    S_W_ID,
    S_I_ID
)
    )
PARTITION BY RANGE (S_W_ID);


CREATE TABLE stock01 PARTITION OF stock
    FOR VALUES FROM (1) TO (3);

CREATE TABLE stock02 PARTITION OF stock
    FOR VALUES FROM (3) TO (5);

CREATE TABLE stock03 PARTITION OF stock
    FOR VALUES FROM (5) TO (7);

CREATE TABLE stock04 PARTITION OF stock
    FOR VALUES FROM (7) TO (9);

CREATE TABLE stock05 PARTITION OF stock
    FOR VALUES FROM (9) TO (11);