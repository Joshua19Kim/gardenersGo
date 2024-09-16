// This code is for missing the path on VM.
// Copy & paste this before your script code; <script th:src="@{/javascript/getBaseUrl.js}"></script>
// The usage is; eg.  `${getBaseUrl()}/images/icons/add_circle_white.png`

function getBaseUrl() {
    const path = window.location.pathname.split('/');
    if (path[1] === 'test') {
        return '/test';
    } else if (path[1] === 'prod') {
        return '/prod';
    }
    return '';
}