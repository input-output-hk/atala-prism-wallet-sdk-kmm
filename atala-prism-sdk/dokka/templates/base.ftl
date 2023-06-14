<#import "includes/page_metadata.ftl" as page_metadata>
<#import "includes/header.ftl" as header>
<#import "includes/footer.ftl" as footer>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1" charset="UTF-8">
    <@page_metadata.display/>
    <@template_cmd name="pathToRoot"> .
        <script>var pathToRoot = "/wallet-sdk-kmm/";//${pathToRoot}</script>
    </@template_cmd>
    <#-- This script doesn't need to be there but it is nice to have
    since app in dark mode doesn't 'blink' (class is added before it is rendered) -->
    <script>const storage = localStorage.getItem("dokka-dark-mode")
        if (storage == null) {
            const osDarkSchemePreferred = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
            if (osDarkSchemePreferred === true) {
                document.getElementsByTagName("html")[0].classList.add("theme-dark")
            }
        } else {
            const savedDarkMode = JSON.parse(storage)
            if(savedDarkMode === true) {
                document.getElementsByTagName("html")[0].classList.add("theme-dark")
            }
        }
    </script>
    <script>
        window.addEventListener('load', function() {
            let links = Object.entries(document.links)
                .map(link => link[1]);

            links.filter(link => link.attributes.href.value.startsWith("-atala-prism-s-d-k"))
                .forEach(a => a.attributes.href.value = '/wallet-sdk-kmm/' + a.attributes.href.value);
            links.filter(link => link.attributes.href.value.startsWith("../"))
                .forEach(a => {
                    let temp = a.attributes.href.value.substr(3, a.attributes.href.value.length - 3);
                    if (temp.startsWith("-atala-prism-s-d-k")) {
                        a.attributes.href.value = '/wallet-sdk-kmm/' + temp;
                    } else if (temp.startsWith("-")) {
                        a.attributes.href.value = '/wallet-sdk-kmm/-atala-prism-s-d-k/' + temp;
                    }
                });
            links.filter(link => link.attributes.href.value.startsWith("../../"))
                .forEach(a => {
                    let temp = a.attributes.href.value.substr(6, a.attributes.href.value.length - 6);
                    if (temp.startsWith("-atala-prism-s-d-k")) {
                        a.attributes.href.value = '/wallet-sdk-kmm/' + temp;
                    } else if (temp.startsWith("-")) {
                        a.attributes.href.value = '/wallet-sdk-kmm/-atala-prism-s-d-k/' + temp;
                    }
                });
            links.filter(link => link.attributes.href.value.startsWith("../../../"))
                .forEach(a => {
                    let temp = a.attributes.href.value.substr(9, a.attributes.href.value.length - 9);
                    if (temp.startsWith("-atala-prism-s-d-k")) {
                        a.attributes.href.value = '/wallet-sdk-kmm/' + temp;
                    } else if (temp.startsWith("-")) {
                        a.attributes.href.value = '/wallet-sdk-kmm/-atala-prism-s-d-k/' + temp;
                    }
                });
        });
    </script>
    <#-- Resources (scripts, stylesheets) are handled by Dokka.
    Use customStyleSheets and customAssets to change them. -->
    <@resources/>
</head>
<body>
<@header.display/>
<div id="container">
    <div id="leftColumn">
        <div id="sideMenu"></div>
    </div>
    <div id="main">
        <@content/>
        <@footer.display/>
    </div>
</div>
</body>
</html>