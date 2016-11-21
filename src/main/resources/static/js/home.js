var globalState = {};

function showLoginForm() {
    $("#div-login").css("visibility", "visible");
    $(".visitorinfo").addClass("blurred");
    $("#log-in-username").focus();
}

function hideLoginForm() {
    $("#div-login").css("visibility", "collapse");
    clearLoginForm();
    $(".visitorinfo").removeClass("blurred");
}

function showSignupForm() {
    $("#div-signup").css("visibility", "visible");
    $(".visitorinfo").addClass("blurred");
    $("#sign-up-username").focus();
}

function hideSignupForm() {
    $("#div-signup").css("visibility", "collapse");
    clearSignupForm();
    $(".visitorinfo").removeClass("blurred");
}

function clearSignupForm() {
    $("#sign-up-username").val("");
    $("#sign-up-email").val("");
    $("#sign-up-password").val("");
    $("#sign-up-message").html("&nbsp;");
}

function clearLoginForm() {
    $("#log-in-username").val("");
    $("#log-in-password").val("");
    $("#log-in-message").html("&nbsp;");
}

function login() {
    $.ajax({
        url: "/api/user/login",
        type: "POST",
        data: JSON.stringify($("#log-in-form").serializeObject()),
        cache: "false",
        contentType: "application/json",
        beforeSend: function() {
            $("#log-in-button").prop("disabled", true);
            $("#log-in-message").html("&nbsp;");
        },
        success: function(xhr, status, error) {
            document.cookie = "userId=" + xhr.userId;
            window.location.replace("protected.html");
        },
        error: function(xhr, status, error) {
            $("#log-in-message").html(xhr.status == 401 ? "Invalid username or password" : xhr.responseText);
        }
    });
}

function signup() {
    $.ajax({
        url: "/api/user/signup",
        type: "POST",
        data: JSON.stringify($("#sign-up-form").serializeObject()),
        cache: "false",
        contentType: "application/json",
        beforeSend: function() {
            $("#sign-up-button").prop("disabled", true);
            $("#sign-up-message").html("&nbsp;");
        },
        success: function() {
            hideSignupForm();
            showLoginForm();
        },
        error: function(xhr, status, error) {
            $("#sign-up-message").html(xhr.responseText);
        },
        complete: function() {
            $("#sign-up-button").prop("disabled", false);
        }
    });
}

function logout() {
    $.ajax({
        url: "/api/user/logout",
        type: "POST",
        cache: "false",
        contentType: "application/json",
        beforeSend: function() {
            deleteCookie("userId");
        },
        success: function() {
            window.location.replace("index.html");
        },
    });
}

function hideAllContainers() {
    $("#albums-container").css("display", "none");
    $("#groups-list-container").css("display", "none");
    $("#group-details-container").css("display", "none");
    $("#image-view").css("display", "none");
}

function clearMenuItems() {
    $(".menuitem").removeClass("activeMenuItem");
}

function menuAlbums() {
    hideAllContainers();
    clearMenuItems();
    $("#menu-albums").addClass("activeMenuItem");
    $("#albums-container").css("display", "");
    listAlbums();
}

function menuGroups() {
    hideAllContainers();
    clearMenuItems();
    $("#menu-groups").addClass("activeMenuItem");
    $("#groups-list-container").css("display", "");
    listGroups();
}

function createAlbumGoBackItem(parent) {
    var str = "";
    if (parent == null)
        str += "<div class='listItem' onclick='listAlbums()'>";
    else
        str += "<div class='listItem' onclick='openAlbum("+parent+")'>";
    str += "<div class='listItemImage'><div style='width:180px;height:180px;display:table;overflow:hidden;'><div style='display:table-cell;vertical-align:middle;'>";
    str += "<img src='images/arrow.png' />";
    str += "</div></div></div>";
    str += "<div class='listItemName' title='Go back to parent album'>Go up</div></div>";
    return str;
}

function createAlbumItem(album) {
    var str = "<div class='listItem' onclick='openAlbum("+album.id+")'>";
    str += "<div class='listItemImage'><div style='width:180px;height:180px;display:table;overflow:hidden;'><div style='display:table-cell;vertical-align:middle;'>";
    str += "<img src='images/album.png' />";
    str += "<div class='album-owner'>Owner: " + album.owner.username + "</div>";
    str += "</div></div></div>";
    str += "<div class='listItemName' title='"+album.name+"'>"+album.name+"</div></div>";
    return str;
}

function createPictureItem(picture) {
    var str = "<div class='listItem' onclick='openPicture("+picture.id+","+picture.albumId+")'>";
    str += "<div class='listItemImage'><div style='width:180px;height:180px;display:table;overflow:hidden;'><div style='display:table-cell;vertical-align:middle;'>";
    str += "<img src='data:image/jpeg;base64," + picture.content + "' />";
    str += "</div></div></div>";
    str += "<div class='listItemName'>&nbsp;</div></div>";
    return str;
}

function listAlbums() {
    var jqxhr = $.get({
        url: "/api/album/",
        cache: "false",
        contentType: "application/json"
    });

    jqxhr.done(function (albumList) {
        $("#create-album").css("display", "");
        $("#download-album-button").css("display", "none");
        $("#delete-album-button").css("display", "none");
        $("#share-album-div").css("display", "none");
        $("#upload-file-div").css("display", "none");

        var albumsListDiv = $("#albums-list");
        albumsListDiv.html("");
        globalState.currentAlbum = null;
        if(albumList.length == 0) {
            $(albumsListDiv).append("<p align='center'>You don't have any albums yet. It's time to create one.</p>");
        } else {
            $(albumsListDiv).append($.map(albumList, createAlbumItem).join(""));
        }
    });
}

function openAlbum(id) {
    if (id == null) {
        listAlbums();
    } else {
        var jqxhr = $.get({
            url: "/api/album/" + id,
            cache: "false",
            contentType: "application/json"
        });

        jqxhr.done(function (album) {
            var albumsList = $("#albums-list");
            albumsList.html("");


            var downloadAlbumButton = $("#download-album-button");
            downloadAlbumButton.css("display", "none");

            var deleteAlbumButton = $("#delete-album-button");
            deleteAlbumButton.css("display", "none");

            var uploadFileDiv = $("#upload-file-div");

            if(album.owner.id == userId()) {
                uploadFileDiv.css("display", "")
                deleteAlbumButton.css("display", "");
                deleteAlbumButton.off("click").on("click", function (e) {
                    e.preventDefault();
                    deleteAlbum(album.id, album.parent);
                });
            } else {
                $("#create-album").css("display", "none");
                uploadFileDiv.css("display", "none");
            }

            albumsList.append(createAlbumGoBackItem(album.parent));

            if (album.albumList.length != 0 || album.pictureList.length != 0) {
                downloadAlbumButton.css("display", "");
                downloadAlbumButton.off("click").on("click", function(e) {
                    e.preventDefault();
                    window.location.href = "/api/album/" + album.id + "/download";
                });
            } else {
                downloadAlbumButton.css("display", "none");
            }

            if (album.parent == null && album.owner.id == userId()) {
                populateGroupSelector();
                $("#share-album-div").css("display", "");
            } else
                $("#share-album-div").css("display", "none");

            albumsList.append(
                $.map(album.albumList, createAlbumItem).join("")
            );
            albumsList.append(
                $.map(album.pictureList, createPictureItem).join("")
            );

            globalState.currentAlbum = album.id;
        });
    }
}

function populateGroupSelector() {
    $.ajax({
        url: "/api/group/",
        type: "GET",
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            $("#group-selector").html("");
            $.each(xhr.data, function(i, item) {
                $("#group-selector").append("<option value='"+item.id+"'>"+item.name+"</option>");
            });
        }
    });
}

function createAlbum() {
    var requestBody = $("#create-album").serializeObject();
    var albumName = $("#create-album-name").val();
    if(albumName == "" || albumName == null || albumName === undefined) {
        alert("Please give a name to your album");
    } else {
        requestBody.parentAlbum = globalState.currentAlbum;
        $.ajax({
            url: "/api/album/",
            type: "POST",
            data: JSON.stringify(requestBody),
            cache: "false",
            contentType: "application/json",
            error: function (xhr, status, error) {
                alert(xhr.responseText);
            },
            complete: function () {
                openAlbum(globalState.currentAlbum);
            }
        });
    }
}

function uploadPicture(){
    var selectedFile = $("#upload-file-form")[0];
    if($("#upload-file-input")[0].files.length == 0) {
        alert("Please select a file to upload.");
    } else {
        $.ajax({
            url: "/api/picture/" + globalState.currentAlbum,
            type: "POST",
            data: new FormData(selectedFile),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function () {
                // Handle upload success
                openAlbum(globalState.currentAlbum);
            },
            error: function () {
                // Handle upload error
                alert("File not uploaded (perhaps it's too much big)\nFile shouldn't be larger than 3 MB");
            }
        });
    }
}

function openPicture(id, albumId) {
    var jqxhr = $.get({
        url: "/api/picture/" + id,
        cache: "false",
        contentType: "application/json"
    });

    jqxhr.done(function(picture) {
        $("#image-view-content").html("<img src='data:image/jpeg;base64," + picture.originalContent + "' />");
        $("#image-view").css("display", "");

        $("#download-picture-button").off("click").on("click", function(e) {
            e.preventDefault();
            window.location.href = "/api/picture/" + picture.id + "/download";
        });
        var deletePictureButton = $("#delete-picture-button");
        deletePictureButton.off("click").on("click", function(e) {
            e.preventDefault();
            deletePicture(albumId, picture.id);
        });
        if(picture.owner.id == userId()) {
            deletePictureButton.css("display", "");
        } else {
            deletePictureButton.css("display", "none");
        }
    });
}

function closePicture() {
    $("#image-view").css("display", "none");
    $("#image-view-content").html("");
}

function createGroupListItem(group) {
    var str = "<div class='groupListItem' onclick='openGroup("+group.id+");'>";
    str += "<div class='groupListItemRow'>Name: "+group.name+"</div>";
    str += "<div class='groupListItemRow'>Owner: "+group.owner.username+"</div>";
    str += "</div>";
    return str;
}

function listGroups() {
    $.ajax({
        url: "/api/group/",
        type: "GET",
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            $("#groups-list").html("");
            $.each(xhr.data, function(i, item) {
                $("#groups-list").append(createGroupListItem(item));
            });
        }
    });
}

function createGroup() {
    $.ajax({
        url: "/api/group/",
        type: "PUT",
        data: JSON.stringify($("#create-group-form").serializeObject()),
        cache: "false",
        contentType: "application/json",
        beforeSend: function() {
            $("#create-group-button").prop("disabled", true);
        },
        success: function() {
            listGroups();
        },
        error: function(xhr, status, error) {
            alert(xhr.responseText);
        },
        complete: function() {
            $("#create-group-button").prop("disabled", false);
            $("#create-group-name").val("");
        }
    });
}

function deleteGroup() {
    $.ajax({
        url: "/api/group/" + currentGroup,
        type: "DELETE",
        cache: "false",
        contentType: "application/json",
        beforeSend: function() {
            $("#delete-group-button").prop("disabled", true);
        },
        success: function() {
            menuGroups();
        },
        error: function(xhr, status, error) {
            alert(xhr.responseText);
        },
        complete: function() {
            $("#delete-group-button").prop("disabled", false);
        }
    });
}

function openGroup(id) {
    $.ajax({
        url: "/api/group/"+id,
        type: "GET",
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            clearGroupDetails();

            var group = xhr.data;

            $("#group-details-name").html(group.name);
            $("#group-details-owner").html(group.owner.username);
            $.each(group.members, function(i, item) {
                $("#group-details-members").append("<li>"+item.username+" <a href='javascript:removeMember("+item.id+");'>Remove</a></li>");
            });

            $("#delete-group-button").off("click").on("click", function() {
                deleteGroup();
            });

            currentGroup = id;

            $("#groups-list-container").css("display", "none");
            $("#group-details-container").css("display", "");

            getGroupAlbums();
        }
    });
}

function getGroupAlbums() {
    $.ajax({
        url: "/api/group/"+currentGroup+"/albums",
        type: "GET",
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            $.each(xhr, function(i, item) {
                $("#group-details-albums").append("<li>"+item.name+"</li>");
            });
        }
    });
}

function clearGroupDetails() {
    $("#group-details-name").html("");
    $("#group-details-owner").html("");
    $("#group-details-albums").html("");
    $("#group-details-members").html("");
}

function removeMember(id) {
    $.ajax({
        url: "/api/group/"+currentGroup+"/removemember",
        type: "POST",
        data: JSON.stringify({"id":+id}),
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            openGroup(currentGroup);
        },
        error: function(xhr, status, error) {
            alert(xhr.responseText);
        }
    });
}

function findUser() {
    $.ajax({
        url: "/api/user/finduser",
        type: "POST",
        data: JSON.stringify({"query":$("#find-user-query").val()}),
        cache: "false",
        contentType: "application/json",
        beforeSend: function() {
            $("#find-user-results").html("");
        },
        success: function(xhr, status, error) {
            $.each(xhr.users, function(i, item) {
                $("#find-user-results").append("<li>"+item.username+" <a href='javascript:;' onclick='addMember("+item.id+")'>Add to group</a></li>");
            });
        }
    });
}

function addMember(id) {
    $.ajax({
        url: "/api/group/"+currentGroup+"/addmember",
        type: "POST",
        data: JSON.stringify({"id":id}),
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            openGroup(currentGroup);
        },
        error: function(xhr, status, error) {
            alert(xhr.responseText);
        }
    });
}

function shareAlbum() {
    var selectedGroup = $("#group-selector option:selected");
    if (selectedGroup == null || selectedGroup === undefined) {
        alert("Select a group first");
        return;
    }

    $.ajax({
        url: "/api/album/"+globalState.currentAlbum+"/share",
        type: "POST",
        data: JSON.stringify({"groupId":selectedGroup.val()}),
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            alert("Album successfully shared with group");
        },
        error: function(xhr, status, error) {
            alert(xhr.responseText);
        }
    });
}

function deleteAlbum(albumId, parentId) {
    $.ajax({
        url: "/api/album/"+albumId,
        type: "DELETE",
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            openAlbum(parentId);
        },
        error: function () {
            alert("Album is shared. You can not delete it.");
        }
    });
}

function deletePicture(parentId, pictureId) {
    $.ajax({
        url: "/api/picture/"+pictureId,
        type: "DELETE",
        cache: "false",
        contentType: "application/json",
        success: function(xhr, status, error) {
            closePicture();
            openAlbum(parentId);
        },
        error: function () {
            alert("Delete failed");
        }
    });
}