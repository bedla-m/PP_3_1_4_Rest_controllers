async function loadUsers() {

    try {
        const response = await fetch('/api/controller');

        if (response.ok) {
            const users = await response.json();
            const tbody = document.getElementById('users_table_script')

            tbody.innerHTML = '';

            users.forEach(user => {
                const roles = user.roles.sort((a, b) => a.id - b.id)
                    .map(role => role.roleName)
                    .join(', ');

                tbody.innerHTML += `
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.surname}</td>
                <td>${user.age}</td>
                <td>${user.username}</td>
                <td>${roles}</td>
                
                <td>
                <button type="button" class="btn btn-info btn-sm" onclick="openEditModal(${user.id})">
                Edit
                </button></td>
                <td>
                <button type="button" class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">
                Delete
                </button></td>
            </tr>
        `;
            });
        } else {
            alert("Ошибка Http: " + response.status);
        }
    } catch (error) {
        console.error('ошибка!', error);
    }
}

async function openCurrentUser() {
    try {
        const response = await fetch('/api/controller/me');

        if (response.ok) {
            const currentUser = await response.json();

            const tbody = document.getElementById("currentUser");
            document.getElementById('currentUsername').textContent = currentUser.username;

            tbody.innerHTML = '';

            const roles = currentUser.roles
                .sort((a, b) => a.id - b.id)
                .map(role => role.roleName)
                .join(', ');

            tbody.innerHTML = `
            <tr>
                <td>${currentUser.id}</td>
                <td>${currentUser.name}</td>
                <td>${currentUser.surname}</td>
                <td>${currentUser.age}</td>
                <td>${currentUser.username}</td>
                <td>${roles}</td>
                </tr> `;
        } else {
            alert("Ошибка открытия юзера: " + response.status);
        }

    } catch (error) {
        console.error('ошибка!', error)
    }

}

async function logout() {
    try {
        await fetch('/logout', {
            method: 'POST'
        });
        window.location.href = '/login';
    } catch (error) {
        console.error("Ошибка выхода! ", error)
    }
}

async function loadRoles() {
    try {
        const response = await fetch('/api/controller/roles');

        if (response.ok) {
            const roles = await response.json();

            const editRoles = document.getElementById("editRoles");
            const addRoles = document.getElementById("addRoles");

            editRoles.innerHTML = '';
            addRoles.innerHTML = '';

            roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.text = role.roleName;

                editRoles.add(option);
                addRoles.add(option.cloneNode(true));
            });
        } else {
            alert("Ошибка при обработке ролей");
        }
    } catch (error) {
        console.error("Ошибка в катч! ", error)
        alert("Не удалось загрузить роли!")
    }
}

async function deleteUser(id) {
    const response = await fetch(`/api/controller/${id}`, {
        method: 'DELETE'
    });
    if (response.ok) {
        loadUsers().then(() => console.log('Пользоватлеь удален!'));
    } else {
        console.log('Ошибка! Пользовательне был удален!')
    }
}

async function init() {
    const me = await fetch('/api/controller/me').then(r => r.json());
    const isAdmin = me.roles && me.roles.some(r => r.roleName === 'ROLE_ADMIN');

    document.getElementById('admin-panel').style.display = isAdmin ? 'block' : 'none';

    const tabId = isAdmin ? '#admin-section' : '#user-section';
    document.querySelector(`#side-menu a[href="${tabId}"]`).click();

    if (isAdmin) {
        await loadUsers();
        await loadRoles();
    }
    await openCurrentUser();
}

async function openEditModal(id) {
    const response = await fetch(`/api/controller/${id}`);
    const user = await response.json();

    document.getElementById('editId').value = user.id;
    document.getElementById('editName').value = user.name;
    document.getElementById('editSurname').value = user.surname;
    document.getElementById('editAge').value = user.age;
    document.getElementById('editUsername').value = user.username;

    const editRolesSelect = document.getElementById('editRoles');
    Array.from(editRolesSelect.options)
        .forEach(option => {
            option.selected = user.roles.some(r => r.id === parseInt(option.value));
        });

    $('#editModal').modal('show');
}

async function saveNewUser() {
    try {
        const user = {
            id: document.getElementById("editNewId").value,
            name: document.getElementById("editNewName").value,
            surname: document.getElementById("editNewSurname").value,
            age: document.getElementById("editNewAge").value,
            username: document.getElementById("editNewUsername").value,
            password: document.getElementById("editNewPassword").value,
            roleIds: Array.from(document.getElementById('addRoles').selectedOptions)
                .map(opt => parseInt(opt.value))
        }

        const responseJson = await fetch('/api/controller', {
            method: 'POST',
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(user)
        });
        if (responseJson.ok) {
            loadUsers().then(() => console.log('Пользоватлеь добавлен!'));
            document.getElementById('users-tab').click();
            document.getElementById('addUserForm').reset();
        } else {
            alert("Ошибка загрузки!");
        }
    } catch (error) {
        console.error("Ошибка в катч! ", error);
        alert("Не удалось сохранить юзера!")
    }
}

async function saveEditUser() {
    try {
        const user = {
            id: document.getElementById("editId").value,
            name: document.getElementById("editName").value,
            surname: document.getElementById("editSurname").value,
            age: document.getElementById("editAge").value,
            username: document.getElementById("editUsername").value,
            password: document.getElementById("editPassword").value,
            roleIds: Array.from(document.getElementById('editRoles').selectedOptions)
                .map(opt => parseInt(opt.value))
        }
        const responseJson = await fetch(`/api/controller/${user.id}`, {
            method: 'PUT',
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(user)
        });
        if (responseJson.ok) {
            $('#editModal').modal('hide');
            await loadUsers();
        } else {
            alert("Ошибка загрузки!");
        }
    } catch {
        alert("Не удалось сохранить юзера!")
    }
}

document.addEventListener('DOMContentLoaded', init);

document.getElementById('editForm')
    .addEventListener('submit', function (event) {
        event.preventDefault();
        saveEditUser().then(() => console.log('Сохранено!'));
    });


document.getElementById('addUserForm')
    .addEventListener('submit', function (event) {
        event.preventDefault();
        saveNewUser().then(() => console.log('Сохранено!'));
    });

document.getElementById('logoutBtn')
    .addEventListener('click', logout);