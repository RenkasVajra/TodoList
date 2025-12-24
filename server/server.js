const express = require("express");
const cors = require("cors");
const { v4: uuidv4 } = require("uuid");

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Request logging middleware
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);
  if (req.method === "POST" || req.method === "PUT") {
    console.log("Body:", JSON.stringify(req.body, null, 2));
  }
  next();
});

// Bearer token authentication middleware
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];

  if (token === "572ba7cd-ae09-42e2-8e3a-c54554a40c13") {
    next();
  } else {
    console.log("Unauthorized access attempt");
    res.status(401).json({ status: "error", message: "Unauthorized" });
  }
};

// In-memory storage for todos
let todos = [
  {
    uid: "1",
    text: "РџСЂРёРјРµСЂ Р·Р°РґР°С‡Рё",
    importance: "РћР±С‹С‡РЅР°СЏ",
    color: null,
    deadline: "31-12-2025",
    isDone: false
  }
];

// API Routes

// GET /api/todos - Get all todos
app.get("/api/todos", authenticateToken, (req, res) => {
  console.log("GET /api/todos - Returning todos");
  res.json({
    status: "success",
    data: { todos }
  });
});

// GET /api/todos/:id - Get todo by ID
app.get("/api/todos/:id", authenticateToken, (req, res) => {
  const todo = todos.find(t => t.uid === req.params.id);
  if (todo) {
    res.json({
      status: "success",
      data: todo
    });
  } else {
    res.status(404).json({
      status: "error",
      message: "Todo not found"
    });
  }
});

// POST /api/todos - Create new todo
app.post("/api/todos", authenticateToken, (req, res) => {
  console.log("POST /api/todos - Creating new todo");

  const newTodo = {
    uid: req.body.uid || uuidv4(),
    text: req.body.text,
    importance: req.body.importance || "РћР±С‹С‡РЅР°СЏ",
    color: req.body.color,
    deadline: req.body.deadline,
    isDone: req.body.isDone || false
  };

  todos.push(newTodo);
  console.log("Todo created successfully:", newTodo);

  res.status(201).json({
    status: "success",
    data: newTodo
  });
});

// PUT /api/todos/:id - Update todo
app.put("/api/todos/:id", authenticateToken, (req, res) => {
  const index = todos.findIndex(t => t.uid === req.params.id);
  if (index !== -1) {
    todos[index] = {
      ...todos[index],
      ...req.body,
      uid: req.params.id // Ensure uid cannot be changed
    };
    res.json({
      status: "success",
      data: todos[index]
    });
  } else {
    res.status(404).json({
      status: "error",
      message: "Todo not found"
    });
  }
});

// DELETE /api/todos/:id - Delete todo
app.delete("/api/todos/:id", authenticateToken, (req, res) => {
  const index = todos.findIndex(t => t.uid === req.params.id);
  if (index !== -1) {
    const deletedTodo = todos.splice(index, 1)[0];
    res.json({
      status: "success",
      data: deletedTodo
    });
  } else {
    res.status(404).json({
      status: "error",
      message: "Todo not found"
    });
  }
});

// Health check
app.get("/api/health", (req, res) => {
  res.json({ status: "ok", message: "Server is running" });
});

app.listen(PORT, () => {
  console.log(`Todo API Server is running on http://localhost:${PORT}`);
  console.log(`API endpoints available at http://localhost:${PORT}/api`);
  console.log(`For Android emulator use: http://10.0.2.2:${PORT}/api`);
});
