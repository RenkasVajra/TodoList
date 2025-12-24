// POST /api/todos - Create new todo
app.post('/api/todos', authenticateToken, (req, res) => {
  console.log('POST /api/todos - Creating new todo:', req.body);

  const newTodo = {
    uid: req.body.uid || uuidv4(),
    text: req.body.text,
    importance: req.body.importance || 'РћР±С‹С‡РЅР°СЏ',
    color: req.body.color,
    deadline: req.body.deadline,
    isDone: req.body.isDone || false
  };

  todos.push(newTodo);
  console.log('Todo created successfully:', newTodo);

  res.status(201).json({
    status: 'success',
    data: newTodo
  });
});
