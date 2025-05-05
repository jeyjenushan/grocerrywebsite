import { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { assets, dummyOrders, dummyProducts } from "../greencart_assets/assets";
import toast from "react-hot-toast";

export const AppContext = createContext();

export const AppContextProvider = ({ children }) => {
  const navigate = useNavigate();
  const currency = import.meta.env.VITE_CURRENCY;
  const [user, setUser] = useState(true);
  const [isSeller, setIsSeller] = useState(true);
  const [showUserLogin, setShowUserLogin] = useState(false);
  const [products, setProducts] = useState([]);
  const [cartItems, setCartItems] = useState({});
  const [searchQuery, setSearchQuery] = useState({});
  const [myOrders, setMyOrders] = useState([]);
  const [orders, setOrders] = useState([]);


  //fetch Orders

  const fetchOrders=async()=>{
    setOrders(dummyOrders)
  }




  //Fetch All Products
  const fetchProducts = async () => {
    setProducts(dummyProducts);
  };

  //Add Product to CART
  const addToCart = (itemId) => {
    let cartData = structuredClone(cartItems);

    if (cartData[itemId]) {
      cartData[itemId] += 1;
    } else {
      cartData[itemId] = 1;
    }
    setCartItems(cartData);
    toast.success("Added to Cart");
  };

  //update cart item quantity
  const updatedCartItem = (itemId, quantity) => {
    setCartItems((prevCartItems) => ({
      ...prevCartItems, // Copy existing cart items
      [itemId]: quantity, // Update the specific item's quantity
    }));
    toast.success("Cart is updated");
  };

  //Remove from the cart
  const removeFromCart = (itemId) => {
    setCartItems((prevCartItems) => {
      const updatedCart = { ...prevCartItems }; // Shallow copy

      if (updatedCart[itemId] > 1) {
        // Decrease quantity if > 1
        updatedCart[itemId] -= 1;
      } else {
        // Remove item if quantity is 1 (or invalid)
        delete updatedCart[itemId];
      }

      toast.success("Item removed from cart");
      return updatedCart;
    });
  };

  //Get Cart Item Count
  const getCartCount = () => {
    let totalCount = 0;
    for (const item in cartItems) {
      totalCount += cartItems[item];
    }
    return totalCount;
  };

  //Get Cart Total Amount
  const getCartAmount = () => {
    let totalAmount = 0;
    for (const items in cartItems) {
      let itemInfo = products.find((product) => product._id === items);
      if (cartItems[items] > 0) {
        totalAmount += itemInfo.offerPrice * cartItems[items];
      }
    }
    return Math.floor(totalAmount * 100) / 100;
  };

  //fetchMyorders
  const fetchMyOrders = async () => {
    setMyOrders(dummyOrders);
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const value = {
    navigate,
    user,
    setUser,
    isSeller,
    setIsSeller,
    setShowUserLogin,
    showUserLogin,
    products,
    currency,
    addToCart,
    updatedCartItem,
    removeFromCart,
    cartItems,
    searchQuery,
    setSearchQuery,
    getCartAmount,
    getCartCount,
    fetchMyOrders,
    setMyOrders,
    myOrders,
    orders,
    setOrders,
    fetchOrders
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export const useAppContext = () => {
  return useContext(AppContext);
};
