CREATE TABLE IF NOT EXISTS "Film"
(
    "id"           integer auto_increment NOT NULL,
    "name"         varchar(100)           NOT NULL,
    "description"  varchar(200),
    "release_date" date                   NOT NULL,
    "duration"     integer                NOT NULL,
    "rating"       varchar(10)            NOT NULL,
    CONSTRAINT "pk_film" PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "Genre"
(
    "id"      integer auto_increment NOT NULL,
    "film_id" integer                NOT NULL,
    "genre"   varchar(10)            NOT NULL,

    CONSTRAINT "pk_genre" PRIMARY KEY ("id"),
    CONSTRAINT "fk_genre_film_id" FOREIGN KEY ("film_id") REFERENCES "Film" ("id")
);

CREATE TABLE IF NOT EXISTS "User"
(
    "id"         integer auto_increment NOT NULL,
    "email"      varchar(50)            NOT NULL,
    "login"      varchar(200)           NOT NULL,
    "name"       varchar(100)           NOT NULL,
    "local_date" date                   NOT NULL,
    CONSTRAINT "pk_user" PRIMARY KEY ("id")
);

create table "Friendship"
(
    "user_id"   INTEGER               not null,
    "friend_id" INTEGER               not null,
    "status"    CHARACTER VARYING(20) not null,
    constraint "pk_friendship"
        primary key ("user_id", "friend_id"),
    constraint "fk_Friendship_friend_id"
        foreign key ("friend_id") references "User",
    constraint "fk_friendship_user_id"
        foreign key ("user_id") references "User",
    constraint "distinct_friend"
        CHECK ("user_id" <> "friend_id")
);



CREATE TABLE IF NOT EXISTS "User_like"
(
    "film_id" integer NOT NULL,
    "user_id" integer NOT NULL,
    CONSTRAINT "pk_user_like" PRIMARY KEY ("film_id", "user_id"),
    CONSTRAINT "fk_user_like_film_id" FOREIGN KEY ("film_id") REFERENCES "Film" ("id"),
    CONSTRAINT "fk_user_like_user_id" FOREIGN KEY ("user_id") REFERENCES "User" ("id")
)
