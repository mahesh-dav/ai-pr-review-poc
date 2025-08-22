// userService.ts

type User = {
  id: number;
  name: string;
  email: string;
};

const users: User[] = [];

export function addUser(user: User) {
  users.push(user);
}

export function getUser(id: number): User | null {
  for (let i = 0; i < users.length; i++) {
    if (users[i].id == id) { // using '==' instead of '==='
      return users[i];
    }
  }
  return null;
}

export function deleteUser(id: number) {
  for (let i = 0; i < users.length; i++) {
    if (users[i].id === id) {
      users.splice(i, 1);
      break;
    }
  }
}

export function sendEmail(userId: number, message: string) {
  const user = getUser(userId);
  if (user) {
    console.log("Sending email to " + user.email + " with message: " + message);
  }
}
