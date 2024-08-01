import {createTheme} from '@mantine/core'

export const theme = createTheme({
    colors: {
        // Define your custom colors here
        green: ['#f0fff4', '#ccffeb', '#99ffdd', '#66ffcc', '#33ffbe', '#00ffad', '#00e699', '#00cc88', '#00b377', '#009966'],
        yellow: ['#fff9e6', '#fff3cc', '#ffedb3', '#ffe799', '#ffe080', '#ffda66', '#ffd34d', '#ffcc33', '#ffc61a', '#ffbf00'],
        orange: ['#fff4e6', '#ffeacc', '#ffe0b3', '#ffd699', '#ffcc80', '#ffc266', '#ffb84d', '#ffad33', '#ffa31a', '#ff9900']
    },
    primaryColor: 'orange',
    primaryShade: {light: 5, dark: 8},

    components: {
        AppShell: {
            styles: (theme) => ({
                main: {
                    backgroundColor: theme.colors.lime[0]
                },
                header: {
                    backgroundColor: theme.colors.lime[0]
                },
                navbar: {
                    backgroundColor: theme.colors.lime[9],
                    display: 'flex',
                    padding: 'var(--mantine-spacing-xl)',
                    overflowY: 'auto'
                },
                footer: {
                    backgroundColor: theme.colors.green[5]
                }
            })
        },
        Code: {
            styles: (theme) => ({
                root: {
                    backgroundColor: theme.colors.lime[7],
                    color: theme.white
                }
            })
        },
        Button: {
            styles: (theme) => ({
                root: {
                    borderRadius: '0.5rem',
                    padding: '0.5rem 1rem',
                    transition: 'background-color 0.3s'
                }
            })
        },
        NavLink: {
            styles: (theme) => ({
                root: {
                    backgroundColor: theme.colors.lime[9],
                    color: theme.white,
                    display: 'flex',
                    fontSize: theme.fontSizes.sm,
                    borderRadius: theme.radius.sm,
                    fontWeight: 500
                }
            })
        }

        // You can also customize other parts of the theme
        /*    fontFamily: 'Arial, sans-serif',
            headings: { fontFamily: 'Georgia, serif' },
            spacing: { xs: 8, sm: 16, md: 24, lg: 32, xl: 40 },*/
    }
})
