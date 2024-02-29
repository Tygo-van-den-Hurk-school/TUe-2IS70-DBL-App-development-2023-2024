// This file contains all the database functionality related to the 'accounts' table.

const db = require("../dbConfig.js");

// Functions of the module
module.exports = {
  get,
  getByName,
  insert,
  update,
  remove,
  getGroups,
};

// Function to get an account based on the ID
function get(id) {
    let query = db("accounts as a");

    // Check for null id
    if (id) {
      return query // return account with id
        .where("a.id", id)
        .first();
    } else { // return all accounts if null
      return query;
    }
}

// Function to get an account based on the name
function getByName(name) {
    let query = db("accounts as a");

    // Check for null name
    if (name) {
      return query // Return account with name
        .where("a.name", name)
        .first();
    } else { // Return all accounts if null
      return query;
    }
}

// Function to insert an account into the table
function insert(account) {
  return db("accounts")
    .insert(account)
    .then(([id]) => get(id)); // return inserted account (with id)
}

// Function to update an account in the table
function update(id, changes) {
  return db("accounts")
    .where("id", id)
    .update(changes)
    .then(count => (count > 0 ? get(id) : null)); // return updated account
}

// Function to delete an account from the table
function remove(id) {
  return db("accounts")
    .where("id", id)
    .del();
}

// Function to get the groups an account belongs to
function getGroups(id) {

    // Subquery to get the id's of the groups that the account is related to
    subquery = db("groupAccountRelations")
              .select("group_id")
              .where("user_id", id);
  
    // Get the groups with the id list
    return db("groups")
      .whereIn("id", subquery);
}