/**
 * 封装评论
 * @param targetId
 * @param type
 * @param content
 */
function comment2target(targetId, type, content) {
    if (!content) {
        alert("回复内容不能为空");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=b4f7f86249a8a17868a7&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closeable", true);

                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json",
        contentType: "application/json"
    });
}

/**
 * 一级评论
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

/**
 * 二级评论
 * @param e
 */
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comment = $("#comment-" + id);
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        comment.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        // 调用接口，获取评论列表
        var subCommentContainer = $("#comment-" + id);
        // 当已经获取到二级评论列表的时候，不需要重新拉取
        // 默认只有一个子元素--评论框和按钮
        if (subCommentContainer.children().length != 1) {
            // 展开二级评论
            comment.addClass("in");
            // 标记展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h6/>", {
                        "class": "media-heading",
                        "text": comment.user.name
                    })).append($("<span/>", {
                        "text": comment.content
                    })).append($("<menu/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right icon",
                        "text": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12"
                    })
                    commentElement.append(mediaElement);
                    subCommentContainer.prepend(commentElement);
                });
                // 展开二级评论
                comment.addClass("in");
                // 标记展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }

    }
}