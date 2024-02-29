// The account table stores the accounts of the users using the app.

exports.up = function(knex) {
    return knex.schema.createTable("accounts", function(accounts) {
        accounts.increments();

        // Variables
        accounts.string("name", 128).notNullable(); // Account name
        accounts.string("email", 128).notNullable(); // Account email
        accounts.string("password").notNullable(); // Hashed password
        accounts.boolean("moderator").notNullable(); // Whether the account is a moderator

        // Make name and email unique
        accounts.unique(["name"]);
        accounts.unique(["email"]);
    });
};

exports.down = function(knex) {
    return knex.schema.dropTableIfExists("accounts");
};
