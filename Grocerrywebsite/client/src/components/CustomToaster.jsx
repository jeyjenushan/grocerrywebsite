import toast, { Toaster, ToastBar } from "react-hot-toast";
import { motion } from "framer-motion";

const CustomToaster = () => {
  return (
    <Toaster
      position="top-right"
      gutter={12}
      containerStyle={{
        left: 24,
        bottom: 24,
      }}
      toastOptions={{
        duration: 4000,
        success: {
          duration: 3000,
          style: {
            background: "#4fbf8b",
            color: "#fff",
          },
          iconTheme: {
            primary: "#fff",
            secondary: "#10B981",
          },
        },
        error: {
          style: {
            background: "#EF4444",
            color: "#fff",
          },
          iconTheme: {
            primary: "#fff",
            secondary: "#EF4444",
          },
        },
        loading: {
          style: {
            background: "#3B82F6",
            color: "#fff",
          },
        },
        style: {
          padding: "12px 16px",
          borderRadius: "8px",
          boxShadow: "0 4px 12px rgba(0, 0, 0, 0.1)",
          maxWidth: "400px",
        },
      }}
    >
      {(t) => (
        <motion.div
          initial={{ opacity: 0, y: -50 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -50 }}
          transition={{ type: "spring", damping: 25, stiffness: 300 }}
        >
          <ToastBar toast={t}>
            {({ icon, message }) => (
              <>
                {icon}
                <span className="ml-3">{message}</span>
                {t.type !== "loading" && (
                  <button
                    onClick={() => toast.dismiss(t.id)}
                    className="ml-4 p-1 rounded-full hover:bg-gray hover:bg-opacity-20 transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-4 w-4"
                      viewBox="0 0 20 20"
                      fill="currentColor"
                    >
                      <path
                        fillRule="evenodd"
                        d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                        clipRule="evenodd"
                      />
                    </svg>
                  </button>
                )}
              </>
            )}
          </ToastBar>
        </motion.div>
      )}
    </Toaster>
  );
};

export default CustomToaster;
