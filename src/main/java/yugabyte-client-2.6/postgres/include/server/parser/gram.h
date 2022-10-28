/* A Bison parser, made by GNU Bison 3.4.1.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2019 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* Undocumented macros, especially those whose name start with YY_,
   are private implementation details.  Do not rely on them.  */

#ifndef YY_BASE_YY_GRAM_H_INCLUDED
# define YY_BASE_YY_GRAM_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int base_yydebug;
#endif

/* Token type.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    IDENT = 258,
    FCONST = 259,
    SCONST = 260,
    BCONST = 261,
    XCONST = 262,
    Op = 263,
    ICONST = 264,
    PARAM = 265,
    TYPECAST = 266,
    DOT_DOT = 267,
    COLON_EQUALS = 268,
    EQUALS_GREATER = 269,
    LESS_EQUALS = 270,
    GREATER_EQUALS = 271,
    NOT_EQUALS = 272,
    ABORT_P = 273,
    ABSOLUTE_P = 274,
    ACCESS = 275,
    ACTION = 276,
    ADD_P = 277,
    ADMIN = 278,
    AFTER = 279,
    AGGREGATE = 280,
    ALL = 281,
    ALSO = 282,
    ALTER = 283,
    ALWAYS = 284,
    ANALYSE = 285,
    ANALYZE = 286,
    AND = 287,
    ANY = 288,
    ARRAY = 289,
    AS = 290,
    ASC = 291,
    ASSERTION = 292,
    ASSIGNMENT = 293,
    ASYMMETRIC = 294,
    AT = 295,
    ATTACH = 296,
    ATTRIBUTE = 297,
    AUTHORIZATION = 298,
    BACKFILL = 299,
    BACKWARD = 300,
    BEFORE = 301,
    BEGIN_P = 302,
    BETWEEN = 303,
    BIGINT = 304,
    BINARY = 305,
    BIT = 306,
    BOOLEAN_P = 307,
    BOTH = 308,
    BY = 309,
    CACHE = 310,
    CALL = 311,
    CALLED = 312,
    CASCADE = 313,
    CASCADED = 314,
    CASE = 315,
    CAST = 316,
    CATALOG_P = 317,
    CHAIN = 318,
    CHAR_P = 319,
    CHARACTER = 320,
    CHARACTERISTICS = 321,
    CHECK = 322,
    CHECKPOINT = 323,
    CLASS = 324,
    CLOSE = 325,
    CLUSTER = 326,
    COALESCE = 327,
    COLLATE = 328,
    COLLATION = 329,
    COLOCATED = 330,
    COLUMN = 331,
    COLUMNS = 332,
    COMMENT = 333,
    COMMENTS = 334,
    COMMIT = 335,
    COMMITTED = 336,
    CONCURRENTLY = 337,
    CONFIGURATION = 338,
    CONFLICT = 339,
    CONNECTION = 340,
    CONSTRAINT = 341,
    CONSTRAINTS = 342,
    CONTENT_P = 343,
    CONTINUE_P = 344,
    CONVERSION_P = 345,
    COPY = 346,
    COST = 347,
    CREATE = 348,
    CROSS = 349,
    CSV = 350,
    CUBE = 351,
    CURRENT_P = 352,
    CURRENT_CATALOG = 353,
    CURRENT_DATE = 354,
    CURRENT_ROLE = 355,
    CURRENT_SCHEMA = 356,
    CURRENT_TIME = 357,
    CURRENT_TIMESTAMP = 358,
    CURRENT_USER = 359,
    CURSOR = 360,
    CYCLE = 361,
    DATA_P = 362,
    DATABASE = 363,
    DAY_P = 364,
    DEALLOCATE = 365,
    DEC = 366,
    DECIMAL_P = 367,
    DECLARE = 368,
    DEFAULT = 369,
    DEFAULTS = 370,
    DEFERRABLE = 371,
    DEFERRED = 372,
    DEFINER = 373,
    DELETE_P = 374,
    DELIMITER = 375,
    DELIMITERS = 376,
    DEPENDS = 377,
    DESC = 378,
    DETACH = 379,
    DICTIONARY = 380,
    DISABLE_P = 381,
    DISCARD = 382,
    DISTINCT = 383,
    DO = 384,
    DOCUMENT_P = 385,
    DOMAIN_P = 386,
    DOUBLE_P = 387,
    DROP = 388,
    EACH = 389,
    ELSE = 390,
    ENABLE_P = 391,
    ENCODING = 392,
    ENCRYPTED = 393,
    END_P = 394,
    ENUM_P = 395,
    ESCAPE = 396,
    EVENT = 397,
    EXCEPT = 398,
    EXCLUDE = 399,
    EXCLUDING = 400,
    EXCLUSIVE = 401,
    EXECUTE = 402,
    EXISTS = 403,
    EXPLAIN = 404,
    EXTENSION = 405,
    EXTERNAL = 406,
    EXTRACT = 407,
    FALSE_P = 408,
    FAMILY = 409,
    FETCH = 410,
    FILTER = 411,
    FIRST_P = 412,
    FLOAT_P = 413,
    FOLLOWING = 414,
    FOR = 415,
    FORCE = 416,
    FOREIGN = 417,
    FORWARD = 418,
    FREEZE = 419,
    FROM = 420,
    FULL = 421,
    FUNCTION = 422,
    FUNCTIONS = 423,
    GENERATED = 424,
    GLOBAL = 425,
    GRANT = 426,
    GRANTED = 427,
    GREATEST = 428,
    GROUP_P = 429,
    GROUPING = 430,
    GROUPS = 431,
    HANDLER = 432,
    HASH = 433,
    HAVING = 434,
    HEADER_P = 435,
    HOLD = 436,
    HOUR_P = 437,
    IDENTITY_P = 438,
    IF_P = 439,
    ILIKE = 440,
    IMMEDIATE = 441,
    IMMUTABLE = 442,
    IMPLICIT_P = 443,
    IMPORT_P = 444,
    IN_P = 445,
    INCLUDE = 446,
    INCLUDING = 447,
    INCREMENT = 448,
    INDEX = 449,
    INDEXES = 450,
    INHERIT = 451,
    INHERITS = 452,
    INITIALLY = 453,
    INLINE_P = 454,
    INNER_P = 455,
    INOUT = 456,
    INPUT_P = 457,
    INSENSITIVE = 458,
    INSERT = 459,
    INSTEAD = 460,
    INT_P = 461,
    INTEGER = 462,
    INTERSECT = 463,
    INTERVAL = 464,
    INTO = 465,
    INVOKER = 466,
    IS = 467,
    ISNULL = 468,
    ISOLATION = 469,
    JOIN = 470,
    KEY = 471,
    LABEL = 472,
    LANGUAGE = 473,
    LARGE_P = 474,
    LAST_P = 475,
    LATERAL_P = 476,
    LEADING = 477,
    LEAKPROOF = 478,
    LEAST = 479,
    LEFT = 480,
    LEVEL = 481,
    LIKE = 482,
    LIMIT = 483,
    LISTEN = 484,
    LOAD = 485,
    LOCAL = 486,
    LOCALTIME = 487,
    LOCALTIMESTAMP = 488,
    LOCATION = 489,
    LOCK_P = 490,
    LOCKED = 491,
    LOGGED = 492,
    MAPPING = 493,
    MATCH = 494,
    MATERIALIZED = 495,
    MAXVALUE = 496,
    METHOD = 497,
    MINUTE_P = 498,
    MINVALUE = 499,
    MODE = 500,
    MONTH_P = 501,
    MOVE = 502,
    NAME_P = 503,
    NAMES = 504,
    NATIONAL = 505,
    NATURAL = 506,
    NCHAR = 507,
    NEW = 508,
    NEXT = 509,
    NO = 510,
    NONCONCURRENTLY = 511,
    NONE = 512,
    NOT = 513,
    NOTHING = 514,
    NOTIFY = 515,
    NOTNULL = 516,
    NOWAIT = 517,
    NULL_P = 518,
    NULLIF = 519,
    NULLS_P = 520,
    NUMERIC = 521,
    OBJECT_P = 522,
    OF = 523,
    OFF = 524,
    OFFSET = 525,
    OIDS = 526,
    OLD = 527,
    ON = 528,
    ONLY = 529,
    OPERATOR = 530,
    OPTION = 531,
    OPTIONS = 532,
    OR = 533,
    ORDER = 534,
    ORDINALITY = 535,
    OTHERS = 536,
    OUT_P = 537,
    OUTER_P = 538,
    OVER = 539,
    OVERLAPS = 540,
    OVERLAY = 541,
    OVERRIDING = 542,
    OWNED = 543,
    OWNER = 544,
    PARALLEL = 545,
    PARSER = 546,
    PARTIAL = 547,
    PARTITION = 548,
    PASSING = 549,
    PASSWORD = 550,
    PLACING = 551,
    PLANS = 552,
    POLICY = 553,
    POSITION = 554,
    PRECEDING = 555,
    PRECISION = 556,
    PRESERVE = 557,
    PREPARE = 558,
    PREPARED = 559,
    PRIMARY = 560,
    PRIOR = 561,
    PRIVILEGES = 562,
    PROCEDURAL = 563,
    PROCEDURE = 564,
    PROCEDURES = 565,
    PROGRAM = 566,
    PUBLICATION = 567,
    QUOTE = 568,
    RANGE = 569,
    READ = 570,
    REAL = 571,
    REASSIGN = 572,
    RECHECK = 573,
    RECURSIVE = 574,
    REF = 575,
    REFERENCES = 576,
    REFERENCING = 577,
    REFRESH = 578,
    REINDEX = 579,
    RELATIVE_P = 580,
    RELEASE = 581,
    RENAME = 582,
    REPEATABLE = 583,
    REPLACE = 584,
    REPLICA = 585,
    RESET = 586,
    RESTART = 587,
    RESTRICT = 588,
    RETURNING = 589,
    RETURNS = 590,
    REVOKE = 591,
    RIGHT = 592,
    ROLE = 593,
    ROLLBACK = 594,
    ROLLUP = 595,
    ROUTINE = 596,
    ROUTINES = 597,
    ROW = 598,
    ROWS = 599,
    RULE = 600,
    SAVEPOINT = 601,
    SCHEMA = 602,
    SCHEMAS = 603,
    SCROLL = 604,
    SEARCH = 605,
    SECOND_P = 606,
    SECURITY = 607,
    SELECT = 608,
    SEQUENCE = 609,
    SEQUENCES = 610,
    SERIALIZABLE = 611,
    SERVER = 612,
    SESSION = 613,
    SESSION_USER = 614,
    SET = 615,
    SETS = 616,
    SETOF = 617,
    SHARE = 618,
    SHOW = 619,
    SIMILAR = 620,
    SIMPLE = 621,
    SKIP = 622,
    SMALLINT = 623,
    SNAPSHOT = 624,
    SOME = 625,
    SPLIT = 626,
    SQL_P = 627,
    STABLE = 628,
    STANDALONE_P = 629,
    START = 630,
    STATEMENT = 631,
    STATISTICS = 632,
    STDIN = 633,
    STDOUT = 634,
    STORAGE = 635,
    STRICT_P = 636,
    STRIP_P = 637,
    SUBSCRIPTION = 638,
    SUBSTRING = 639,
    SYMMETRIC = 640,
    SYSID = 641,
    SYSTEM_P = 642,
    TABLE = 643,
    TABLEGROUP = 644,
    TABLEGROUPS = 645,
    TABLES = 646,
    TABLESAMPLE = 647,
    TABLESPACE = 648,
    TABLETS = 649,
    TEMP = 650,
    TEMPLATE = 651,
    TEMPORARY = 652,
    TEXT_P = 653,
    THEN = 654,
    TIES = 655,
    TIME = 656,
    TIMESTAMP = 657,
    TO = 658,
    TRAILING = 659,
    TRANSACTION = 660,
    TRANSFORM = 661,
    TREAT = 662,
    TRIGGER = 663,
    TRIM = 664,
    TRUE_P = 665,
    TRUNCATE = 666,
    TRUSTED = 667,
    TYPE_P = 668,
    TYPES_P = 669,
    UNBOUNDED = 670,
    UNCOMMITTED = 671,
    UNENCRYPTED = 672,
    UNION = 673,
    UNIQUE = 674,
    UNKNOWN = 675,
    UNLISTEN = 676,
    UNLOGGED = 677,
    UNTIL = 678,
    UPDATE = 679,
    USER = 680,
    USING = 681,
    VACUUM = 682,
    VALID = 683,
    VALIDATE = 684,
    VALIDATOR = 685,
    VALUE_P = 686,
    VALUES = 687,
    VARCHAR = 688,
    VARIADIC = 689,
    VARYING = 690,
    VERBOSE = 691,
    VERSION_P = 692,
    VIEW = 693,
    VIEWS = 694,
    VOLATILE = 695,
    WHEN = 696,
    WHERE = 697,
    WHITESPACE_P = 698,
    WINDOW = 699,
    WITH = 700,
    WITHIN = 701,
    WITHOUT = 702,
    WORK = 703,
    WRAPPER = 704,
    WRITE = 705,
    XML_P = 706,
    XMLATTRIBUTES = 707,
    XMLCONCAT = 708,
    XMLELEMENT = 709,
    XMLEXISTS = 710,
    XMLFOREST = 711,
    XMLNAMESPACES = 712,
    XMLPARSE = 713,
    XMLPI = 714,
    XMLROOT = 715,
    XMLSERIALIZE = 716,
    XMLTABLE = 717,
    YEAR_P = 718,
    YES_P = 719,
    ZONE = 720,
    NOT_LA = 721,
    NULLS_LA = 722,
    WITH_LA = 723,
    POSTFIXOP = 724,
    NO_OPCLASS = 725,
    EXPR_LIST = 726,
    UMINUS = 727
  };
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
union YYSTYPE
{
#line 237 "gram.y"

	core_YYSTYPE		core_yystype;
	/* these fields must match core_YYSTYPE: */
	int					ival;
	char				*str;
	const char			*keyword;

	char				chr;
	bool				boolean;
	JoinType			jtype;
	DropBehavior		dbehavior;
	OnCommitAction		oncommit;
	List				*list;
	Node				*node;
	Value				*value;
	ObjectType			objtype;
	TypeName			*typnam;
	FunctionParameter   *fun_param;
	FunctionParameterMode fun_param_mode;
	ObjectWithArgs		*objwithargs;
	DefElem				*defelt;
	SortBy				*sortby;
	WindowDef			*windef;
	JoinExpr			*jexpr;
	IndexElem			*ielem;
	Alias				*alias;
	RangeVar			*range;
	IntoClause			*into;
	WithClause			*with;
	InferClause			*infer;
	OnConflictClause	*onconflict;
	A_Indices			*aind;
	ResTarget			*target;
	struct PrivTarget	*privtarget;
	AccessPriv			*accesspriv;
	struct ImportQual	*importqual;
	InsertStmt			*istmt;
	VariableSetStmt		*vsetstmt;
	PartitionElem		*partelem;
	PartitionSpec		*partspec;
	PartitionBoundSpec	*partboundspec;
	RoleSpec			*rolespec;
	OptSplit			*splitopt;
	OptTableGroup		*grpopt;
	RowBounds			*rowbounds;

#line 577 "gram.h"

};
typedef union YYSTYPE YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif

/* Location type.  */
#if ! defined YYLTYPE && ! defined YYLTYPE_IS_DECLARED
typedef struct YYLTYPE YYLTYPE;
struct YYLTYPE
{
  int first_line;
  int first_column;
  int last_line;
  int last_column;
};
# define YYLTYPE_IS_DECLARED 1
# define YYLTYPE_IS_TRIVIAL 1
#endif



int base_yyparse (core_yyscan_t yyscanner);

#endif /* !YY_BASE_YY_GRAM_H_INCLUDED  */
