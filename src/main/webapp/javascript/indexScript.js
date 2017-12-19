$(document).ready(function () {
 /*   var theme = getDemoTheme();
    $('#SplitterVertical1').jqxSplitter({width: 1000, height: 633, theme: theme, panels: [{size: 250}, {size: 550}]});
  $('#SplitterVertical2').jqxSplitter({theme: theme, panels: [{size: 550, collapsible: false}, {size: 200}]});
   $('#SplitterHorizDroit1').jqxSplitter({orientation: 'horizontal', theme: theme, panels: [{size: 205}, {size: 190}]});
   $('#SplitterHorizDroit2').jqxSplitter({orientation: 'horizontal', theme: theme, panels: [{size: 208}, {size: 190}]});*/
});

function selectText(containerid) {
    if (document.selection) {
        var range = document.body.createTextRange();
        range.moveToElementText(document.getElementById(containerid));
        range.select();
    } else if (window.getSelection) {
        var range = document.createRange();
        range.selectNode(document.getElementById(containerid));
        window.getSelection().addRange(range);
    }
}
