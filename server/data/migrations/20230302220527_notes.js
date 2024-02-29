// The notes table stores the drops that users posted on the app.

exports.up = function (knex) {
    return knex.schema.createTable("notes", function (notes) {
        notes.increments();

        notes.string("title", 128).notNullable(); // Note title
        notes.text("message").defaultTo(""); // Note message

        // Note position
        notes.float("longitude").notNullable();
        notes.float("latitude").notNullable();

        notes.boolean("anonymous").defaultTo(false); // Whether note is anonymous
        notes.bigint("time").notNullable(); // Time note is posted in milliseconds after UNIX epoch

        notes.integer("reports").defaultTo(0); // Amount of reports of note
        notes.integer("upvotes").defaultTo(0); // Amount of upvotes of note

        // Group that the note belongs to
        notes
            .integer("group_id")
            .unsigned()
            .notNullable()
            .references("id")
            .inTable("groups")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");

        // User that posted the note
        notes
            .integer("user_id")
            .unsigned()
            .notNullable()
            .references("id")
            .inTable("accounts")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");

        // Username of the user that posted the note
        notes
            .string("username")
            .notNullable()
            .references("name")
            .inTable("accounts")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");
    });
};

exports.down = function (knex) {
    return knex.schema.dropTableIfExists("notes");
};
