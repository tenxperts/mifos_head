ALTER TABLE PPI_SURVEY MODIFY COLUMN COUNTRY_ID INTEGER NOT NULL;

update DATABASE_VERSION set DATABASE_VERSION = 138 where DATABASE_VERSION = 137;