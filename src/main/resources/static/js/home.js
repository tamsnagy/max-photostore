function listAlbums() {
    var jqxhr = $.get({
        url: "/api/album/",
        cache: "false",
        contentType: "application/json"
    });

    jqxhr.done(function (albumList) {
        if(albumList.length == 0) {
            $("#albums-div")
                .append("You don't have any albums yet. It's time to create one.");
        } else {
            $("#albums-table").append(
                $.map(albumList, displayAlbum).join()
            );
        }
    });
}

function openAlbum(id) {
    var jqxhr = $.get({
        url: "/api/album/" + id,
        cache: "false",
        contentType: "application/json"
    });

    jqxhr.done(function (album) {
        //TODO: visualize an album
        $("#albums-div").hide();
        $("#album-view").show();
        $("#albums-in-album").append(
            $.map(album.albumList, displayAlbum).join()
        );
        $("#pictures-in-album").append(
            $.map(album.pictureList, displaySmallPicture).join()
        );


    })
}

function openPicture(id) {
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

