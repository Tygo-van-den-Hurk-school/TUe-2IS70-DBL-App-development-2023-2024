// This file contains all the database functionality related to the 'notes' table.

const db = require("../dbConfig.js");

// Functions of the module
module.exports = {
    get,
    insert,
    update,
    remove,
    getLocationRange,
    getNoteComments,
    getReportedNotes,
};

// Function to get a note based on the ID
function get(id) {
    let query = db("notes as n");

    // Check for valid id
    if (id) { // return note with the id
        return query
            .where("n.id", id)
            .first();
    } else { // return all notes when no valid id
        return query;
    }
}

// Function to insert a note in the table
function insert(note) {
    return db("notes")
        .insert(note)
        .then(([id]) => get(id)); // return inserted note (with id)
}

// Function to update a note in the table
function update(id, changes) {
    return db("notes")
        .where("id", id)
        .update(changes)
        .then(count => (count > 0 ? get(id) : null)); // return updated note
    
}

// Function to delete a note from the table
function remove(id) {
    return db("notes")
        .where("id", id)
        .del();
}

function getLocationRange(minLat, minLong, maxLat, maxLong) {
    return db("notes")
        .whereBetween("latitude", [minLat, maxLat])
        .andWhereBetween("longitude", [minLong, maxLong]);
}

function getNoteComments(noteId) {
    return db("comments")
        .where("note_id", noteId);
}

function getReportedNotes() {
    return db("notes as n").where('n.reports', '>', 0);
}