tailwind.config = {
    theme: {
        extend: {
            colors: {
                primary: {
                    light: '#00A5CF',
                    DEFAULT: '#008793',
                    dark: '#004d7a',
                },
                secondary: {
                    light: '#FFE1A8',
                    DEFAULT: '#FFC172',
                    dark: '#E09F3E',
                }
            },
            fontFamily: {
                inter: ['Inter', 'sans-serif'],
                openSans: ['Open Sans', 'sans-serif'],
                roboto: ['Roboto', 'sans-serif'],
                workSans: ['Work Sans', 'sans-serif']
            },
            animation: {
                'float': 'float 3s ease-in-out infinite',
            },
            keyframes: {
                float: {
                    '0%, 100%': {
                        transform: 'translateY(0)'
                    },
                    '50%': {
                        transform: 'translateY(-10px)'
                    },
                }
            }
        }
    }
}
