# Kanban Board Access

The project's task management is handled via a Kanban board plugin.

## Board Location
The board data is stored locally on the user's machine at:
`~/.kanban/projects.json`

On Windows, this corresponds to:
`C:/Users/fabio/.kanban/projects.json` (or `%USERPROFILE%\.kanban\projects.json`)

## Accessing the Board
To see the current tasks, backlog, and project status, read the `projects.json` file.
The "Drift" project contains the relevant columns:
- **To Do**: Pending tasks and features.
- **In Progress**: Tasks currently being worked on.
- **Done**: Completed tasks.

Always check this board to identify the next priority task ("card") to work on.
Each card includes:
- **Title**: Short description of the task.
- **Description**: Detailed requirements.
- **Priority**: HIGH, MEDIUM, or LOW.
- **Tags**: Categories like Feature, Bug, Spike, etc.
