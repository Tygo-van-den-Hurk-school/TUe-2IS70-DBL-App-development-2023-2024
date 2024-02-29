// This file takes the HTTP requests related to accounts and decides on the correct functionality based on the URL

const express = require("express")

const notesDB = require("../data/helpers/noteModel")

const auth = require("../auth")

const router = express.Router()

router.use(express.json())

// GET request, gets all notes
router.get("/", auth, (req, res) => {
    notesDB.get()
        .then(notes => {
            res.status(200).json(notes)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your notes", err })
        })
})

// GET request, gets all notes with 1 or more reports
router.get('/reported', auth, (req, res) => {
    notesDB.getReportedNotes()
        .then(notes => {
            res.status(200).json(notes)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your notes", err })
        })
})

// POST request, creates a new note
router.post("/", auth, (req, res) => {
    notesDB.insert(req.body)
        .then(notes => {
            res.status(200).json(notes)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong adding your note", err })
        })
})

// GET request, gets the note with the input id
router.get("/:id/", auth, (req, res) => {
    notesDB.get(req.params.id)
        .then(note => {
            res.status(200).json(note)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your note", err })
        })
})

// PUT request, updates a note with the note specified in the body
router.put("/:id", auth, (req, res) => {
    notesDB.update(req.params.id, req.body)
        .then(note => {
            res.status(200).json(note)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong updating your note", err })
        })
})

// DELETE request, deletes a note with a certain id
router.delete("/:id", auth, (req, res) => {
    notesDB.remove(req.params.id, req.body)
        .then(note => {
            res.status(200).json(note)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong deleting your note", err })
        })
})

// GET request, gets all the notes in a certain range
router.get("/:minLat/:minLong/:maxLat/:maxLong", auth, (req, res) => {
    notesDB.getLocationRange(req.params.minLat, req.params.minLong, req.params.maxLat, req.params.maxLong)
        .then(notes => {
            res.status(200).json(notes)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your notes in range", err })
        })
})

// GET request, gets all comments associated with a note
router.get("/:id/comments", auth, (req, res) => {
    notesDB.getNoteComments(req.params.id)
        .then(comments => {
            res.status(200).json(comments)
        })
        .catch(err => {
            res.status(500).json({ message: "Something went wrong getting your project actions", err })
        })
})

module.exports = router
