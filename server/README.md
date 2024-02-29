# Drops

## Description

This server holds the API for the app 'Drops'. It has a database that stores the user accounts, notes posted by the users, comments
placed under the notes and groups that users have created. 

## Project structure

The structure of the code of this project is quite simple. Firstly, the data folder contains the functionality relevant to the database.
This folder contains three subfolders: 'helpers', which contains the functionality for querying a database; 'migrations', which contains the functionality for creating and deleting tables, including the table data; 'seeds', which contains the standard objects that are inserted into the database.

### Database Schemas
The database contains 5 tables for various purposes:

#### Accounts

| Field       | Data Type | Description|
| ----------- | --------- | ---------- |
| id          | integer   | account id |
| name        | string    | account name |
| email       | string    | email of the user |
| password    | string    | hashed password |
| moderator   | boolean   | whether the account is a moderator |

#### Notes

| Field       | Data Type | Description|
| ----------- | --------- | ---------- |
| id          | integer   | note id |
| title       | string    | note title |
| message     | string    | note content |
| longitude   | float     | longitude of note position |
| latitude    | float     | latitude of note position |
| anonymous   | boolean   | whether the note is anonymous |
| time        | bigint    | time the note was posted |
| reports     | integer   | amount of reports the note has |
| upvotes     | integer   | amount of upvotes the note has |
| group_id    | integer   | group that the note belongs to |
| user_id     | integer   | user that posted the note |
| username    | string    | username of the user that posted the note |

#### Comments

| Field       | Data Type | Description|
| ----------- | --------- | ---------- |
| id          | integer   | comment id |
| note_id     | string    | note that the comment is associated to |
| user_id     | string    | user that posted the comment |
| username    | string    | username of user that posted the comment |
| text        | boolean   | text of the comment |
| reports     | boolean   | amount of reports the comment has |

#### Groups

| Field       | Data Type | Description|
| ----------- | --------- | ---------- |
| id          | integer   | group id   |
| name        | string    | group name |

#### Group - Account relations

| Field       | Data Type | Description|
| ----------- | --------- | ---------- |
| id          | integer   | relation id   |
| group_id    | integer   | group that the user is associated with  |
| user_id     | integer   | user that belongs to the group |

## 
This project was forked from the Github repository https://github.com/MorbidMiyako/node-api-challenge, which was in turn forked from https://github.com/bloominstituteoftechnology/node-api-challenge