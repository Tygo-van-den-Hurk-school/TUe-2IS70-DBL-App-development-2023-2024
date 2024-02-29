// The comments table stores the comments that users posted on the app.

exports.up = function(knex) {
    return knex.schema.createTable("comments", function(comments) {
        comments.increments();

        // Note that the comment belongs to
        comments
            .integer("note_id")
            .unsigned()
            .notNullable()
            .references("id")
            .inTable("notes")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");

        // User that posted the comment
        comments
            .integer("user_id")
            .unsigned()
            .notNullable()
            .references("id")
            .inTable("accounts")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");

        // Username of user that posted the comment
        comments
            .string("username")
            .notNullable()
            .references("name")
            .inTable("accounts")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");

        comments.text("text").notNullable(); // Text of the comment
        comments.integer("reports").defaultTo(0); // Amount of reports the comments has
    });  
};

exports.down = function(knex) {
    return knex.schema.dropTableIfExists("comments");
};
