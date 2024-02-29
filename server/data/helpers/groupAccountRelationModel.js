// This file contains all the database functionality related to the 'groupAccountRelations' table.

const db = require("../dbConfig.js");

// Functions of the module
module.exports = {
  get,
  insert,
  update,
  remove,
};

// Function to get a specific relation based on the ID
function get(id) {
    let query = db("groupAccountRelations as g");

    // Check for null id
    if (id) { // return relation with id
      return query
        .where("g.id", id)
        .first();
    } else { // return all relations if null
      return query;
    }
}

// Function to insert a relation in the table
function insert(GARel) {
  return db("groupAccountRelations")
    .insert(GARel)
    .then(([id]) => get(id));
}

// Function to update a relation in the table
function update(id, changes) {
  return db("groupAccountRelations")
    .where("id", id)
    .update(changes)
    .then(count => (count > 0 ? get(id) : null));
}

// Function to delete a relation from the table
function remove(id) {
  return db("groupAccountRelations")
    .where("id", id)
    .del();
}