var globalState = {};

function listGroups() {
    refreshGroups();
    $("#groups-div").show();
    $("#group-details-div").hide();
    $("#album-view").hide();
    $("#albums-div").hide();
    $("#upload-file-form").hide();
}

function listAlbums() {
    var jqxhr = $.get({
        url: "/api/album/",
        cache: "false",
        contentType: "application/json"
    });

    jqxhr.done(function (albumList) {
        $("#albums-table").html("");
        var albumsDiv = $("#albums-div");
        if(albumList.length == 0) {
            albumsDiv
                .append("<p>You don't have any albums yet. It's time to create one.</p>");
        } else {
            $("#albums-table").append(
                $.map(albumList, displayAlbum).join()
            );
        }
        globalState.currentAlbum = null;
        displayIdOfCurrentAlbum();
        albumsDiv.show();
        clearAlbumViewDiv();
        hideGroupsList();
        hideGroupDetails();
        $("#upload-file-form").hide();
    });
}

function uploadPicture(){
    displayIdOfCurrentAlbum();
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
                $("#upload-file-message").text("File succesfully uploaded");
            },
            error: function () {
                // Handle upload error
                $("#upload-file-message").text(
                    "File not uploaded (perhaps it's too much big)");
            }
        });
    }
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
                $("#sign-up-message").text(xhr.responseText);
            },
            complete: function () {
                openAlbum(globalState.currentAlbum);
            }
        });
    }
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
            // hide albums-div
            $("#albums-table").html("");
            clearAlbumViewDiv();
            hideGroupsList();
        hideGroupDetails();
            $("#album-view").show();

            if (album.parent == null) {
                $("#go-back-to-album").append("<button class='myButton' onclick='listAlbums()'>Go up a level</button>");
            } else {
                $("#go-back-to-album").append("<button class='myButton' onclick='openAlbum(" + album.parent + ")'> Go up a level</button>");
            }
            $("#albums-in-album").append(
                $.map(album.albumList, displayAlbum).join()
            );
            $("#pictures-in-album").append(
                $.map(album.pictureList, displaySmallPicture).join()
            );

            globalState.currentAlbum = album.id;
            displayIdOfCurrentAlbum();

            $("#upload-file-form").show();
        });
    }
}

function openPicture(id) {
    displayIdOfCurrentAlbum();
    console.log("Picture open with id " + id);
}

function displayAlbum(album) {
    return "<tr onclick='openAlbum(" + album.id + ")'><td>" +
        "<table><tr><td>" + album.name +
        "</td></tr><tr><td>" + album.owner.username + "</td></tr></table></td></tr>";
}

function displaySmallPicture(picture) {
    return "<tr onclick='openPicture(" + picture.id + ")'><td>" +
        "<img src='data:image/jpeg;base64," + picture.content + "' /></td></tr>";
}

function displayIdOfCurrentAlbum() {
    $("#current-album-display-tag").html("Current album id " + globalState.currentAlbum);
}

function clearAlbumViewDiv(){
    $("#go-back-to-album").html("");
    $("#albums-in-album").html("");
    $("#pictures-in-album").html("");
}

function hideGroupsList() {
    $("#groups-div").hide();
}

function hideGroupDetails() {
    $("#group-details-div").hide();
}