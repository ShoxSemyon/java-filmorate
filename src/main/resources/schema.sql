CREATE TABLE IF NOT EXISTS "Genre"
(
    "id"   integer      NOT NULL,
    "name" varchar(255) NOT NULL,
    CONSTRAINT "pk_genre" PRIMARY KEY ("id")

);
CREATE TABLE IF NOT EXISTS "Rating"
(
    "id"   integer      NOT NULL,
    "name" varchar(255) NOT NULL,
    CONSTRAINT "pk_rating" PRIMARY KEY ("id")

);

CREATE TABLE IF NOT EXISTS "Film"
(
    "id"           integer auto_increment NOT NULL,
    "name"         varchar(255)           NOT NULL,
    "description"  varchar(255),
    "release_date" date                   NOT NULL,
    "duration"     integer                NOT NULL,
    "rating"       integer                NOT NULL,
    CONSTRAINT "pk_film" PRIMARY KEY ("id"),
    CONSTRAINT "fk_film_rating_id" FOREIGN KEY ("rating") REFERENCES "Rating" ("id")
);

CREATE TABLE IF NOT EXISTS "Film_genre"
(
    "id"       integer auto_increment NOT NULL,
    "film_id"  integer                NOT NULL,
    "genre_id" integer                NOT NULL,

    CONSTRAINT "pk_film_genre" PRIMARY KEY ("id"),
    CONSTRAINT "fk_film_genre_film" FOREIGN KEY ("film_id") REFERENCES "Film" ("id"),
    CONSTRAINT "fk_film_genre_genre" FOREIGN KEY ("genre_id") REFERENCES "Genre" ("id")

);


CREATE TABLE IF NOT EXISTS "User"
(
    "id"         integer auto_increment NOT NULL,
    "email"      varchar(255)           NOT NULL,
    "login"      varchar(255)           NOT NULL,
    "name"       varchar(255)           NOT NULL,
    "local_date" date                   NOT NULL,
    CONSTRAINT "pk_user" PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "Friendship"
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
