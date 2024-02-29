// This file contains all the database functionality related to the 'comments' table.

const db = require("../dbConfig.js");

// Functions of the module
module.exports = {
    get,
    insert,
    update,
    remove,
    getReportedComments,
};

// Function to get a comment based on the ID
function get(id) {
    let query = db("comments as c");

    // Check for valid id
    if (id) { // return comment with the id
        return query
            .where("c.id", id)
            .first();
    } else { // return all comments when no valid id
        return query;
    }
}

// Function to insert a comment in the table
function insert(comment) {
    return db("comments")
        .insert(comment)
        .then(([id]) => get(id)); // return inserted commment (with id)
}

// Function to update a comment in the table
function update(id, changes) {
    return db("comments")
        .where("id", id)
        .update(changes)
        .then(count => (count > 0 ? get(id) : null)); // return updated comment
}

// Function to delete a comment from the table
function remove(id) {
    return db("comments")
        .where("id", id)
        .del();
}

// Function to get all comments with 1 or more reports
function getReportedComments() {
    return db("comments").where('reports', '>', 0);
}