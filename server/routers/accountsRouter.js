// This file takes the HTTP requests related to accounts and decides on the correct functionality based on the URL

const express = require("express")
const bcryptjs = require('bcryptjs');
const jwt = require('jsonwebtoken');

const accountsDB = require("../data/helpers/accountModel")
const groupDB = require("../data/helpers/groupModel")

const auth = require("../auth")

const router = express.Router()

router.use(express.json())

// Get all accounts for the base 'GET' request
router.get("/", auth, (req, res) => {
    accountsDB.get()
        .then(accounts => {
            res.status(200).json(accounts)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your accounts", err })
        })
})

// base 'POST' request functionality   
router.post("/", auth, (req, res) => {
    accountsDB.insert(req.body)
        .then(accounts => {
            res.status(200).json(accounts)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong adding your account", err })
        })
})

// Functionality for creating accounts
router.post('/create', async (req, res) => {
    try {
        // Get input from request body
        const { name, email, password, moderator } = req.body;

        // Make sure the input is non-null
        if (!name || !email || !password) {
            return res.status(500).json({ message: "Empty field found", err })
        }

        // Encrypt password
        const hash_password = await bcryptjs.hash(password, 12);

        // Insert the account into the database and store the inserted account (now with id)
        let users;
        users = await accountsDB.insert({
            name: name, email: email, moderator: moderator, password: hash_password
        })

        // Add every new account to the 'Public' group
        publicGroup = await groupDB.addMember(1, users.id);

        // Get the user access token using JWT
        const user_id = { user_id: users };
        const token = jwt.sign(user_id, "key");

        // Make sure that the account inserted correctly
        if (!users) {
            return res.status(500).json({ message: "something went wrong creating your account", err })
        }

        // Return the authentication token and the user id
        const id = users.id
        return res.status(200).json({ token, id })

    } catch (err) {
        // Catch any possible errors
        return res.status(500).json({ message: "something went wrong creating your account", err })
    }
})

// Functionality for logging in to an existing account
router.post('/login', async (req, res) => {
    try {
        // Get input from request body
        const { name, password } = req.body;

        // Make sure input is not null
        if (!name || !password) {
            return res.status(500).json({ message: "Empty field found" })
        }

        // Get the account corresponding to the name
        const users = await accountsDB.getByName(name).first();
        if (!users) {
            return res.status(500).json({ message: "Invalid name" })
        }

        // Compare the password by encrypting it
        const compare_password = await bcryptjs.compare(password, users.password);

        if (compare_password) {
            // If the password is correct, return access token and the user id
            const user_id = { user_id: users.id };
            const token = jwt.sign(user_id, "key");
            const id = users.id
            return res.status(200).json({ token, id })
        } else {
            // Failed login
            return res.status(500).json({ message: "Invalid name and password", err })
        }

    } catch (err) {
        // Catch any possible errors
        return res.status(500).json({ message: "something went wrong logging in your account", err })
    }
})

// GET request with specified id, returns the account with the input id
router.get("/:id/", auth, (req, res) => {
    accountsDB.get(req.params.id)
        .then(account => {
            res.status(200).json(account)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your account", err })
        })
})

// Gets the groups associated with an account
router.get("/:id/groups", auth, (req, res) => {
    accountsDB.getGroups(req.params.id)
      .then(groups => {
        res.status(200).json(groups)
      })
      .catch(err => {
        res.status(500).json({ message: "something went wrong getting group members", err })
      })
})

// Alternative method of getting accounts with the name of the account
router.get("/name/:name/", auth, (req, res) => {
    accountsDB.getByName(req.params.name)
        .then(account => {
            res.status(200).json(account)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your account", err })
        })
})

// Functionalty for updating an account with the id with the body
router.put("/:id", auth, (req, res) => {
    accountsDB.update(req.params.id, req.body)
        .then(account => {
            res.status(200).json(account)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong updating your account", err })
        })
})

// Deleting an account with a certain id
router.delete("/:id", auth, (req, res) => {
    accountsDB.remove(req.params.id, req.body)
        .then(account => {
            res.status(200).json(account)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong deleting your account", err })
        })
})

module.exports = router
