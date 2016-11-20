var globalState = {};

function jqShow(jqSelector){
    jqSelector.removeAttr("hidden");
    jqSelector.show();
}

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
        globalState.currentAlbum = null;
        clearAlbumViewDiv();
        clearPictureView();
        hideGroupsList();
        hideGroupDetails();
        $("#upload-file-form").hide();
        var albumsDiv = $("#albums-div");
        if(albumList.length == 0) {
            $("#album-view-warning")
                .append("<p>You don't have any albums yet. It's time to create one.</p>");
        } else {
            $("#albums-table").append(
                $.map(albumList, displayAlbum).join()
            );
        }
        jqShow(albumsDiv);
    });
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
            $("#albums-table").html("");
            clearAlbumViewDiv();
            clearPictureView();
            hideGroupsList();
            hideGroupDetails();
            jqShow($("#album-view"));

            if (album.parent == null) {
                $("#go-back-to-album").append("<button class='myButton' onclick='listAlbums()'>Go up a level</button>");
            } else {
                $("#go-back-to-album").append("<button class='myButton' onclick='openAlbum(" + album.parent + ")'> Go up a level</button>");
            }
            if (album.albumList.length != 0 || album.pictureList.length != 0) {
                $("#album-view-download").html(
                    "<form method='GET' action='/api/album/" + album.id + "/download'><input class='myButton', type='submit' value='Download album'/></form>"
                );
            } else {
                $("#album-view-download").html(
                    "<p>This album is empty.<br/>Create a new album inside, or upload a photo.</p>"
                );
            }
            $("#albums-in-album").append(
                $.map(album.albumList, displayAlbum).join()
            );
            $("#pictures-in-album").append(
                $.map(album.pictureList, displaySmallPicture).join()
            );

            globalState.currentAlbum = album.id;

            jqShow($("#upload-file-form"));
        });
    }
}

function openPicture(id) {
    clearAlbumViewDiv();
    clearPictureView();
    hideGroupDetails();
    hideGroupsList();

    var jqxhr = $.get({
        url: "/api/picture/" + id,
        cache: "false",
        contentType: "application/json"
    });

    jqxhr.done(function(picture) {
        var pictureViewDiv = $("#picture-view");
        pictureViewDiv.html(
            "<button class='myButton' onclick='openAlbum(" + globalState.currentAlbum + ")' >Go back to album</button>" +
            "<form method='GET' action='/api/picture/" + id + "/download'><input type='submit' class='myButton' value='Download picture'></form>" +
            "<p>Name: " + picture.name + "</p>" +
            "<img src='data:image/jpeg;base64," + picture.originalContent + "' />"
        );
        jqShow(pictureViewDiv);
    });
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

function clearAlbumViewDiv(){
    $("#album-view-warning").html("");
    $("#go-back-to-album").html("");
    $("#album-view-download").html("");
    $("#albums-in-album").html("");
    $("#pictures-in-album").html("");
}

function clearPictureView() {
    $("#picture-view").html("");
}

function hideGroupsList() {
    $("#groups-div").hide();
}

function hideGroupDetails() {
    $("#group-details-div").hide();
}